package svkreml.krekerCa.tsaServer.tsaServlet;


import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SimpleAttributeTableGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.*;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class TsaServer extends HttpServlet {
    CertAndKey tsaCertAndKey;

    public TsaServer(CertAndKey tsaCertAndKey) {
        this.tsaCertAndKey = tsaCertAndKey;
    }

    public void setTsaCertAndKey(CertAndKey tsaCertAndKey) {
        this.tsaCertAndKey = tsaCertAndKey;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {

            InputStream inputStream = request.getInputStream();
            int available = inputStream.available();
            byte[] bytes = new byte[available];
            inputStream.read(bytes);


            response.addHeader("Content-Type", "application/timestamp-query");
            response.addHeader("Accept", "application/tsp-response");


            TsaServer tsaServer = new TsaServer(tsaCertAndKey);
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

        try {
            DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();
            sigBuilder = new JcaSignerInfoGeneratorBuilder(digestCalculatorProvider);
        } catch (OperatorCreationException e) {
            throw new RuntimeException("Could not create signature info generator", e);
        }
        addAttributs(sigBuilder);
        try {
            SignerInfoGenerator signerInfoGen = sigBuilder.build(new JcaContentSignerBuilder(CryptoAlgFactory.getInstance(tsaCertAndKey.getPrivateKey().getAlgorithm()).getSignatureAlgorithm()).setProvider("BC").
                    build(tsaCertAndKey.getPrivateKey()), tsaCertAndKey.getCertificate());
            DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();
            DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);

            TimeStampTokenGenerator timeStampTokenGenerator = new TimeStampTokenGenerator(signerInfoGen, digestCalculator, new ASN1ObjectIdentifier("1.1.1"));

            addTsaSigningCert(timeStampTokenGenerator);

            return timeStampTokenGenerator;
        } catch (OperatorCreationException | CertificateEncodingException | IOException | TSPException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void addAttributs(JcaSignerInfoGeneratorBuilder sigBuilder) {
        AttributeTable attributes = new AttributeTable(new Hashtable<String, String>());
        sigBuilder.setSignedAttributeGenerator(
                new DefaultSignedAttributeTableGenerator(attributes));
        sigBuilder.setUnsignedAttributeGenerator(new SimpleAttributeTableGenerator(attributes));
    }

    private void addTsaSigningCert(TimeStampTokenGenerator timeStampTokenGenerator) throws IOException, CertificateEncodingException {
        ArrayList<X509CertificateHolder> certList = new ArrayList<>();
        certList.add(new X509CertificateHolder(tsaCertAndKey.getCertificate().getEncoded()));
        Store<X509CertificateHolder> store = new CollectionStore<>(certList);
        timeStampTokenGenerator.addCertificates(store);
    }
}
