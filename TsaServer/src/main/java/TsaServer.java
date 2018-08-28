import caJava.Utils.MeUtils;
import caJava.core.CertAndKey;
import caJava.core.cryptoAlg.CryptoAlgFactory;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SimpleAttributeTableGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.*;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class TsaServer extends HttpServlet {

    CertAndKey tsaCertAndKey;

    public TsaServer() {
    }

    public TsaServer(CertAndKey tsaCertAndKey) {
        this.tsaCertAndKey = tsaCertAndKey;
    }

    public TsaServer(File cert, File pkey) throws IOException {
        MeUtils.loadBC();
        System.out.println("1");
        X509Certificate x509Certificate = CertEnveloper.decodeCert(FileManager.read(cert));
        PrivateKey privateKey = CertEnveloper.decodePrivateKey(pkey);
        this.tsaCertAndKey = new CertAndKey(privateKey, x509Certificate);
        System.out.println("2");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {
            System.out.println("3");
            InputStream inputStream = request.getInputStream();
            int available = inputStream.available();
            byte[] bytes = new byte[available];
            inputStream.read(bytes);


            response.addHeader("Content-Type", "application/timestamp-query");
            response.addHeader("Accept", "application/tsp-response");


            String cert = "ConsoleApp/exampleKeys/testTsa.der";
            String pkey = "ConsoleApp/exampleKeys/testTsa.der.pkey";
            TsaServer tsaServer = new TsaServer(new File(cert), new File(pkey));
            TimeStampRequest timeStampRequest = new TimeStampRequest(bytes);

            TimeStampResponse timeStampResponse = tsaServer.generateTimeStampResponse(timeStampRequest);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(timeStampResponse.getEncoded());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    TimeStampResponse generateTimeStampResponse(TimeStampRequest timestampRequest) {

        TimeStampTokenGenerator tokenGenerator = createTokenGenerator();

        TimeStampResponseGenerator respGen = new TimeStampResponseGenerator(
                tokenGenerator, TSPAlgorithms.ALLOWED);
        Date tsDate = new Date();
        TimeStampResponse response;
        try {
            response = respGen.generate(timestampRequest, tsaCertAndKey.getCertificate().getSerialNumber(), tsDate);
        } catch (Exception e) {
            throw new RuntimeException("Could not generate timestamp response", e);
        }
        return response;
    }

    private TimeStampTokenGenerator createTokenGenerator() {
        JcaSignerInfoGeneratorBuilder sigBuilder;
        DigestCalculatorProvider digestCalculatorProvider;
        try {
            digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();
            sigBuilder = new JcaSignerInfoGeneratorBuilder(digestCalculatorProvider);
        } catch (OperatorCreationException e) {
            throw new RuntimeException("Could not create signature info generator", e);
        }

        AttributeTable attributes = new AttributeTable(new Hashtable<String, String>());
        sigBuilder.setSignedAttributeGenerator(
                new DefaultSignedAttributeTableGenerator(attributes));
        sigBuilder.setUnsignedAttributeGenerator(new SimpleAttributeTableGenerator(attributes));

        SignerInfoGenerator signerInfoGen;
        try {
            signerInfoGen = sigBuilder.build(new JcaContentSignerBuilder(CryptoAlgFactory.getInstance(tsaCertAndKey.getPrivateKey().getAlgorithm()).signatureAlgorithm).setProvider("BC").build(tsaCertAndKey.getPrivateKey()), tsaCertAndKey.getCertificate());
        } catch (Exception e) {
            throw new RuntimeException("Could not create signer info generator", e);
        }
        try {

            DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);
            TimeStampTokenGenerator timeStampTokenGenerator = new TimeStampTokenGenerator(signerInfoGen, digestCalculator, new ASN1ObjectIdentifier("1.2.643.2.2.38.4"));
            ArrayList<X509CertificateHolder> certList = new ArrayList<>();
            certList.add(new X509CertificateHolder(tsaCertAndKey.getCertificate().getEncoded()));
            Store<X509CertificateHolder> store = new CollectionStore(certList);
            timeStampTokenGenerator.addCertificates(store);
            return timeStampTokenGenerator;
        } catch (Exception e) {
            throw new RuntimeException("Could not create timestamp token generator", e);
        }
    }
}
