package sandbox.pkcs12;

import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class PfxWriter {

    public static void main(String[] args) throws IOException, KeyStoreException {
        String password = "1234567890";

       // X509Certificate cert = CertEnveloper.decodeCert(FileManager.read(new File("etalon.der")));
        X509Certificate cert = CertEnveloper.decodeCert(FileManager.read(new File("cer_t.der")));
        //PrivateKey pkey = CertEnveloper.decodePrivateKey(new File("etalon.pkey"));
        PrivateKey pkey = CertEnveloper.decodePrivateKey(new File("cer_t.pkey"));

        Security.addProvider(new BouncyCastleProvider());


        PKCS12KeyStoreSpi.BCPKCS12KeyStore ks = new PKCS12KeyStoreSpi.BCPKCS12KeyStore();
        ks.engineSetKeyEntry("key", pkey, password.toCharArray(), new Certificate[]{cert});
        OutputStream os = new FileOutputStream("p12.pfx");
        ks.engineStore(os, password.toCharArray());

        //ks.engineLoad(stream, "1234567890".toCharArray());
       // String keyName = (String) ks.engineAliases().nextElement();
      //  System.out.println(ks.getClass().load());
      //  System.out.println(ks.engineSize());
    }
}
