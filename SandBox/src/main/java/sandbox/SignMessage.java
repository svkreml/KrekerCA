package sandbox;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

public class SignMessage {


    public static void main(String[] args) throws Exception {


        Security.addProvider(new BouncyCastleProvider());

        X509Certificate cert = CertEnveloper.decodeCert(FileManager.read(new File("etalon.der")));
        PrivateKey privKey = CertEnveloper.decodePrivateKey(new File("etalon.pkey"));
        CryptoAlg cryptoAlg = CryptoAlgGost2001.getCryptoAlg();
        byte[] bytes = FileManager.read(new File("helloWorld.txt"));
        CMSTypedData msg = new CMSProcessableByteArray(bytes);




        //Sign
        //PrivateKey privKey = (PrivateKey) key;
        Signature signature = Signature.getInstance(cryptoAlg.signatureAlgorithm, "BC");
        signature.initSign(privKey);
        signature.update(bytes);

        //Build CMS
        //X509Certificate cert = (X509Certificate) ks.getCertificate(KEYSTORE_ALIAS);
        List certList = new ArrayList();
        //CMSTypedData msg = new CMSProcessableByteArray(text.getBytes());
        certList.add(cert);
        Store certs = new JcaCertStore(certList);
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner sha1Signer = new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm).setProvider("BC").build(privKey);
        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(sha1Signer, cert));
        gen.addCertificates(certs);
        CMSSignedData sigData = gen.generate(msg, false);

        FileOutputStream sigfos = new FileOutputStream("signature_1.sig");
            sigfos.write(Base64.encode(sigData.getEncoded()));
            sigfos.close();
    }
}