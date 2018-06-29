package sandbox.cms;

import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CMSsign {
    public static void main(String[] args) throws CMSException, OperatorCreationException, CertificateEncodingException, IOException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
            //logger.info("Криптопровайдер BC был загружен");
        }
        X509Certificate signCert = CertEnveloper.decodeCert(FileManager.read(new File("etalon.der")));
        PrivateKey caCertPrivateKey = CertEnveloper.decodePrivateKey(new File("etalon.pkey"));
        CryptoAlg cryptoAlg = CryptoAlgGost2001.getCryptoAlg();
        CMSTypedData msg = new CMSProcessableByteArray(FileManager.read(new File("helloWorld.txt")));
        List certList = new ArrayList();




        certList.add(signCert);

        Store certs = new JcaCertStore(certList);

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner sha1Signer = new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm).setProvider(cryptoAlg.cryptoProvider).build(caCertPrivateKey);

        gen.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(
                        new JcaDigestCalculatorProviderBuilder().setProvider(cryptoAlg.cryptoProvider).build())
                        .build(sha1Signer, signCert));

        gen.addCertificates(certs);

        CMSSignedData sigData = gen.generate(msg, false);

        FileManager.write(new File("helloWorld.sig"), Base64.getEncoder().encode(sigData.getEncoded()));

    }
}
