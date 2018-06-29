package caJava.core;

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