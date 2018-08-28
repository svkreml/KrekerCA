import caJava.core.BcInit;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OcspServer extends HttpServlet {


    static List<RevokedCertificate> revokedCertificates;

    static {
        BcInit.init();

        RevokationListBuilder revokationListBuilder = new RevokationListBuilder();
        revokedCertificates = revokationListBuilder.getTestList();
    }

    public static OCSPResp generateOCSPResponse(OCSPReq request, PrivateKey ocspPrivateKey, X509Certificate ocspCert) throws NoSuchProviderException, OCSPException, IOException, OperatorCreationException, CertificateEncodingException {


        BasicOCSPRespBuilder basicOCSPRespBuilder = getBasicOCSPRespBuilder(ocspCert);

        ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BouncyCastleProvider.PROVIDER_NAME).build(ocspPrivateKey);
        X509CertificateHolder[] chain = getCertChain();
        BasicOCSPResp basicResp = getBasicOCSPRespBuilder(ocspCert).build(signer, chain, new Date());
        OCSPRespBuilder respGen = new OCSPRespBuilder();

       //перекладываем  nonce, падаем если её нету
        try {
            putNonceFromRequestToResponce(request, basicOCSPRespBuilder);
        } catch (NullPointerException e) {
            return respGen.build(OCSPRespBuilder.MALFORMED_REQUEST, basicResp);
        }
        // смотрим все запросы
        iterateOcspRequests(basicOCSPRespBuilder, request.getRequestList());



        BasicOCSPResp basicOCSPResp = basicOCSPRespBuilder.build(signer, chain, Calendar.getInstance().getTime() );
        return respGen.build(OCSPRespBuilder.SUCCESSFUL, basicOCSPResp);
        //return respGen.build(OCSPRespBuilder.SUCCESSFUL, basicOCSPRespBuilder);
    }

    private static void putNonceFromRequestToResponce(OCSPReq request, BasicOCSPRespBuilder basicOCSPRespGenerator) throws IOException {
        Extension extension = request.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);//"1.3.6.1.5.5.7.48.1.2"
        if(extension == null) throw new NullPointerException("ocsp_nonce должен быть");
        ExtensionsGenerator extGen = new ExtensionsGenerator();
        extGen.addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, extension);
        basicOCSPRespGenerator.setResponseExtensions(extGen.generate());
    }

    private static void iterateOcspRequests(BasicOCSPRespBuilder basicOCSPRespGenerator, Req[] requests) throws OCSPException, IOException, CertificateEncodingException {
        goToNextRequest:
        for (Req req : requests) {
            CertificateID certID = req.getCertID();


            if(!certID.matchesIssuer(new X509CertificateHolder(caCert.getEncoded()), new BcDigestCalculatorProvider())){
                basicOCSPRespGenerator.addResponse(certID, new UnknownStatus());
                continue goToNextRequest;
            }

            for (RevokedCertificate revokedCertificate : revokedCertificates) {
                {
                    if (certID.equals(revokedCertificate.getCertificateID())) {
                        Date nextUpdate = new Date(new Date().getTime() + 10_000_000);
                        basicOCSPRespGenerator.addResponse(certID, revokedCertificate.getRevokedStatus(), new Date(), nextUpdate);
                        continue goToNextRequest;
                    }
                }
            }
            basicOCSPRespGenerator.addResponse(certID, CertificateStatus.GOOD);
        }
    }

    private static X509CertificateHolder[] getCertChain() throws IOException, CertificateEncodingException {
        X509CertificateHolder[] chain = new X509CertificateHolder[1];
        chain[0] = new X509CertificateHolder(caCert.getEncoded());
        return chain;
    }

    private static BasicOCSPRespBuilder getBasicOCSPRespBuilder(X509Certificate ocspCert) throws IOException, OperatorCreationException, OCSPException {
        //fixme тут какая-то дичь
        ASN1InputStream aIn = new ASN1InputStream(ocspCert.getPublicKey().getEncoded());
        SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(aIn.readObject());
        AlgorithmIdentifier algorithmIdentifier = info.getAlgorithm();
        SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, ocspCert.getPublicKey().getEncoded());

        //создание digestCalculator
        JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
        DigestCalculatorProvider digestCalculatorProvider = digestCalculatorProviderBuilder.build();
        DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);


        return new BasicOCSPRespBuilder(subjectPublicKeyInfo, digestCalculator);
    }
  static   X509Certificate caCert;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            InputStream inputStream = request.getInputStream();
            int available = inputStream.available();
            byte[] bytes = new byte[available];
            inputStream.read(bytes);
            final OCSPReq ocspReq = new OCSPReq(bytes);

            response.addHeader("Content-Type", "application/ocsp-request");
            response.addHeader("Accept", "application/ocsp-response");

            //read OCSP certificate
            File caFile = new File("Server/OcspServer/ca.der");
            bytes = FileManager.read(caFile);
           caCert = CertEnveloper.decodeCert(bytes);
            File pkeyFile = new File("Server/OcspServer/ca.der.pkey");



            OCSPResp ocspResp = generateOCSPResponse(ocspReq, CertEnveloper.decodePrivateKey(pkeyFile),  caCert);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(ocspResp.getEncoded());
            outputStream.flush();
            outputStream.close();
        } catch (NoSuchProviderException | OCSPException | OperatorCreationException | CertificateEncodingException e) {
            e.printStackTrace();
        }
    }
}
