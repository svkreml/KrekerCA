package svkreml.krekerCa.core.pfx;

import org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import svkreml.krekerCa.core.BcInit;
import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.fileManagement.CertEnveloper;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Logger;

public class PfxUtils {
    private static Logger logger = Logger.getLogger(PfxUtils.class.getName());

    public static CertAndKey convertToCertAndKey(File file, String password) {
        BcInit.init();
        try (FileInputStream stream = new FileInputStream(file)) {
            Security.addProvider(new BouncyCastleProvider());
            PKCS12KeyStoreSpi.BCPKCS12KeyStore ks = new PKCS12KeyStoreSpi.BCPKCS12KeyStore();
            ks.engineLoad(stream, password.toCharArray());

            logger.info("Ключей в контейнере: " + ks.engineSize());
            Enumeration enumeration = ks.engineAliases();
            //ks.load
            while (enumeration.hasMoreElements()) {
                Object nextElement = enumeration.nextElement();
                logger.info("Алиас: " + nextElement);
                Key key = ks.engineGetKey(nextElement.toString(), password.toCharArray());
                Certificate cert = ks.engineGetCertificate(nextElement.toString());
                //FileManager.write(new File("outputCer/test.pkey"), CertEnveloper.encodePrivateKey((PrivateKey) key));
                return new CertAndKey(new KeyPair(cert.getPublicKey(), (PrivateKey) key), CertEnveloper.decodeCert(cert.getEncoded()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CertAndKey loadPfx(String file, String password) {
        return convertToCertAndKey(new File(file), password);
    }

    public static void convertToPfx(CertAndKey certAndKey, String alias, String password, File file) throws IOException, KeyStoreException {
        BcInit.init();
        logger.info("Создание контейнера ключей для:\n\t " + certAndKey.getCertificate().getIssuerX500Principal());

        // PKCS12KeyStoreSpi.BCPKCS12KeyStore ks = new PKCS12KeyStoreSpi.BCPKCS12KeyStore();
        // ks.engineSetKeyEntry("key", pkey, password.toCharArray(), new Certificate[]{cert});
        // OutputStream os = new FileOutputStream("p12.pfx");
        // ks.engineStore(os, password.toCharArray());

        X509Certificate cert = certAndKey.getCertificate();
        PrivateKey pkey = certAndKey.getPrivateKey();

        PKCS12KeyStoreSpi.BCPKCS12KeyStore ks = new PKCS12KeyStoreSpi.BCPKCS12KeyStore();
        ks.engineSetKeyEntry(alias, pkey, password.toCharArray(), new Certificate[]{cert});
        OutputStream os = new FileOutputStream(file);
        ks.engineStore(os, password.toCharArray());
        logger.info("alias: " + alias);
        logger.info("Cохранено в файл: " + file);
    }

}
