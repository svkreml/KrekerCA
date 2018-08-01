package caJava.core.extensions;

import org.bouncycastle.cert.X509v3CertificateBuilder;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertBuildContainer {
    Date from;// = new Date(Long.valueOf(params[1]));
    Date to ;//= new Date(Long.valueOf(params[2]));
    private X509v3CertificateBuilder x509v3CertificateBuilder;
    private KeyPair keyPair;
    private X509Certificate caCert;
    public CertBuildContainer(X509v3CertificateBuilder x509v3CertificateBuilder, KeyPair keyPair, X509Certificate caCert,Date from, Date to) {
        this.x509v3CertificateBuilder = x509v3CertificateBuilder;
        this.keyPair = keyPair;
        this.caCert = caCert;
        this.from =from;
        this.to=to;
    }

    public CertBuildContainer(X509v3CertificateBuilder x509v3CertificateBuilder, KeyPair keyPair, X509Certificate caCert) {
        this.x509v3CertificateBuilder = x509v3CertificateBuilder;
        this.keyPair = keyPair;
        this.caCert = caCert;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public X509v3CertificateBuilder getX509v3CertificateBuilder() {
        return x509v3CertificateBuilder;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public X509Certificate getCaCert() {
        return caCert;
    }
}
