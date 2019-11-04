package svkreml.krekerCa.ocspServer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
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
import svkreml.krekerCa.core.cryptoAlg.CryptoAlg;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Slf4j
public class OcspServlet extends HttpServlet {

    private static final UnknownStatus UNKNOWN_STATUS = new UnknownStatus();
    private ContentSigner signer;
    @Setter
    private X509CRL x509CRL;
    private BcDigestCalculatorProvider bcDigestCalculatorProvider = new BcDigestCalculatorProvider();
    private X509CertificateHolder caCertHolder;
    private X509Certificate ocspSigningCert;
    private SubjectPublicKeyInfo subjectPublicKeyInfo;
    private DigestCalculator digestCalculator;

    OcspServlet(X509Certificate caCert, X509Certificate ocspSigningCert, PrivateKey privateKey) throws CertificateEncodingException, IOException, OperatorCreationException, NoSuchAlgorithmException {
        this.caCertHolder = new X509CertificateHolder(caCert.getEncoded());
        this.ocspSigningCert = ocspSigningCert;

        ASN1InputStream aIn = new ASN1InputStream(ocspSigningCert.getPublicKey().getEncoded());
        SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(aIn.readObject());
        AlgorithmIdentifier algorithmIdentifier = info.getAlgorithm();
        subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, ocspSigningCert.getPublicKey().getEncoded());

        //создание digestCalculator
        JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
        DigestCalculatorProvider digestCalculatorProvider = digestCalculatorProviderBuilder.build();
        digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);


        CryptoAlg cryptoAlg = CryptoAlgFactory.getInstance(
                ocspSigningCert.getSigAlgName());
        signer = new JcaContentSignerBuilder(Objects.requireNonNull(cryptoAlg).getSignatureAlgorithm()).setProvider(BouncyCastleProvider.PROVIDER_NAME).build(privateKey);
    }

    private OCSPResp generateOCSPResponse(OCSPReq request) throws OCSPException, IOException, CertificateEncodingException {


        X509CertificateHolder[] chain = getCertChain();

        OCSPRespBuilder respGen = new OCSPRespBuilder();
        //перекладываем  nonce, падаем если её нету
        try {
            BasicOCSPRespBuilder basicOCSPRespBuilder = new BasicOCSPRespBuilder(subjectPublicKeyInfo, digestCalculator);
            Extension extension = request.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);//"1.3.6.1.5.5.7.48.1.2"
            if (extension == null) throw new NullPointerException("ocsp_nonce должен быть");
            ExtensionsGenerator extGen = new ExtensionsGenerator();
            extGen.addExtension(extension);
          //  extGen.addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, extension.getExtnValue());
            basicOCSPRespBuilder.setResponseExtensions(extGen.generate());
            // смотрим все запросы
            iterateOcspRequests(basicOCSPRespBuilder, request);
            BasicOCSPResp basicOCSPResp = basicOCSPRespBuilder.build(signer, chain, Calendar.getInstance().getTime());
            return respGen.build(OCSPRespBuilder.SUCCESSFUL, basicOCSPResp);
        } catch (NullPointerException e) {
            log.error("Ошибка при обработке запроса", e);
            BasicOCSPResp basicResp = new BasicOCSPRespBuilder(subjectPublicKeyInfo, digestCalculator).build(signer, chain, new Date());
            return respGen.build(OCSPRespBuilder.MALFORMED_REQUEST, basicResp);
        }
    }

    private void iterateOcspRequests(BasicOCSPRespBuilder basicOCSPRespGenerator, OCSPReq request) throws OCSPException {
        log.info("*** Processing request ***");
        for (Req req : request.getRequestList()) {
            CertificateID certID = req.getCertID();
            BigInteger checkingCertSerialNumber = certID.getSerialNumber();
            log.info("Сertificate serial number: " + checkingCertSerialNumber.toString(16));
            if (!certID.matchesIssuer(caCertHolder, bcDigestCalculatorProvider)) {

                /*  //
                 * Если сертификат УЦ, который приложен к запросу не наш, то даже не смотрим, а отвечаем "UnknownStatus"
                 * */
                basicOCSPRespGenerator.addResponse(certID, UNKNOWN_STATUS);
                log.info("Set status: UNKNOWN_STATUS");
                continue;
            }

            /*
             * Ищем сертификат в чёрном списке, если он там есть ставим RevokedStatus
             * */
            if (x509CRL != null) {
                X509CRLEntry revokedCertificate = x509CRL.getRevokedCertificate(checkingCertSerialNumber);
                if (revokedCertificate != null) {
                    Date nextUpdate = new Date(new Date().getTime() + 1000 * 60 * 10);
                    int reason = 0;
                    if (revokedCertificate.getRevocationReason() != null) {
                        reason = revokedCertificate.getRevocationReason().ordinal();
                    }

                    basicOCSPRespGenerator.addResponse(
                            certID,
                            new RevokedStatus(
                                    revokedCertificate.getRevocationDate(),
                                    reason
                            ),
                            new Date(),
                            nextUpdate);
                    log.info("Set status: RevokedStatus");
                } else {
                    log.info("Set status: GOOD");
                    basicOCSPRespGenerator.addResponse(certID, CertificateStatus.GOOD);
                }
            } else {
                log.info("Set status: GOOD");
                basicOCSPRespGenerator.addResponse(certID, CertificateStatus.GOOD);
            }
        }
    }

    private X509CertificateHolder[] getCertChain() throws IOException, CertificateEncodingException {
        X509CertificateHolder[] chain = new X509CertificateHolder[1];
        chain[0] = new X509CertificateHolder(ocspSigningCert.getEncoded());
        return chain;
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.addHeader("Content-Type", "application/ocsp-request");
            response.addHeader("Accept", "application/ocsp-response");

            final OCSPReq ocspReq = new OCSPReq(readRequestToBytes(request));
            OCSPResp ocspResp = generateOCSPResponse(ocspReq);
            writeResponse(response, ocspResp);
        } catch (OCSPException | CertificateEncodingException e) {
            log.error("Ошибка при обработке OCSP запроса", e);
        }
    }

    private void writeResponse(HttpServletResponse response, OCSPResp ocspResp) throws IOException {
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(ocspResp.getEncoded());
        outputStream.flush();
        outputStream.close();
    }

    private byte[] readRequestToBytes(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        int available = inputStream.available();
        byte[] bytes = new byte[available];
        int read = inputStream.read(bytes);
        return bytes;
    }
}
