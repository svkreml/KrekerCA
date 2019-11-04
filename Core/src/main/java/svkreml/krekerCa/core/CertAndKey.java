package svkreml.krekerCa.core;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import svkreml.krekerCa.fileManagement.CertEnveloper;
import svkreml.krekerCa.fileManagement.FileManager;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/*
 * вспомогательный класс чтобы можно было взвращать сертификаты и закрытый ключ одним объектом, обновлён до совместимости с цепочкой сертификатов
 * */
public class CertAndKey {
    private final KeyPair keyPair;
    private final X509Certificate[] certificateChain;// = new X509Certificate[1];


    @Deprecated
    public CertAndKey(X509Certificate certificate, KeyPair keyPair) {
        certificateChain = new X509Certificate[1];
        certificateChain[0] = certificate;
        this.keyPair = keyPair;
    }

    public CertAndKey(KeyPair keyPair, X509Certificate... certificateChain) {
        this.certificateChain = certificateChain;
        this.keyPair = keyPair;
    }

    public CertAndKey(PrivateKey privateKey, X509Certificate... certificateChain) {
        if (certificateChain.length < 1) throw new NullPointerException("Нет ни одного сертификата");
        this.certificateChain = certificateChain;
        this.keyPair = new KeyPair(certificateChain[0].getPublicKey(), privateKey);
    }

    public static CertAndKey getPKCS8FromDisk(String certificatePath, String privateKeyPath) throws IOException {

        File ca = new File(certificatePath);
        File caPkey = new File(privateKeyPath);
        byte[] bytes = FileManager.read(ca);
        byte[] caPkeyBytes = FileManager.read(caPkey);
        X509Certificate caCert = CertEnveloper.decodeCert(bytes);
        PrivateKey privateKey = CertEnveloper.decodePrivateKey(caPkey);
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(caPkeyBytes);
     privateKeyInfo.getPrivateKeyAlgorithm();
        return new CertAndKey(privateKey, caCert);
    }

    public X509Certificate[] getCertificateChain() {
        return certificateChain;
    }

    public X509Certificate getCertificate() {
        return certificateChain[0];
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }
}
