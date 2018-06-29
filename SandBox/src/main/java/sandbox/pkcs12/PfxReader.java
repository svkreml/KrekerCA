package sandbox.pkcs12;


import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Enumeration;

public class PfxReader {
    public static void main(String[] args) {
        try (FileInputStream stream = new FileInputStream("data/test_1.pfx")) {
            Security.addProvider(new BouncyCastleProvider());
//        KeyStore ks = KeyStore.getInstance("PKCS12","BC");
            PKCS12KeyStoreSpi.BCPKCS12KeyStore ks = new PKCS12KeyStoreSpi.BCPKCS12KeyStore();
            ks.engineLoad(stream, "1234567890".toCharArray());
            System.out.println(ks.getClass().getName());
            System.out.println(ks.engineSize());
            Enumeration enumeration = ks.engineAliases();
            while (enumeration.hasMoreElements()){
                Object nextElement = enumeration.nextElement();
                System.out.println(nextElement);
                Key key = ks.engineGetKey(nextElement.toString(), "1234567890".toCharArray());
                System.out.println(key);
                FileManager.write(new File("outputCer/test.pkey"), CertEnveloper.encodePrivateKey((PrivateKey) key));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
