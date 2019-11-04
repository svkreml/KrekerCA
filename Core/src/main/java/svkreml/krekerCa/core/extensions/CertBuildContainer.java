package svkreml.krekerCa.core.extensions;

import lombok.Builder;
import lombok.Getter;
import org.bouncycastle.cert.X509v3CertificateBuilder;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;



@Getter
public class CertBuildContainer {


    PublicKey publicKey;
    private Date fromDate;
    private Date toDate;
    private Date privateKeyFromDate;
    private Date privateKeyToDate;
    private X509v3CertificateBuilder x509v3CertificateBuilder;
    private KeyPair keyPair;
    private X509Certificate caCert;

    @Builder
    public CertBuildContainer(PublicKey publicKey, Date fromDate, Date toDate, Date privateKeyFromDate, Date privateKeyToDate, X509v3CertificateBuilder x509v3CertificateBuilder, KeyPair keyPair, X509Certificate caCert) {
        this.publicKey = publicKey;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.privateKeyFromDate = privateKeyFromDate;
        this.privateKeyToDate = privateKeyToDate;
        this.x509v3CertificateBuilder = x509v3CertificateBuilder;
        this.keyPair = keyPair;
        this.caCert = caCert;
    }

    //#region constructors
    @Deprecated
    public CertBuildContainer(X509v3CertificateBuilder x509v3CertificateBuilder, KeyPair keyPair, X509Certificate caCert, Date from, Date to) {
        this.x509v3CertificateBuilder = x509v3CertificateBuilder;
        this.keyPair = keyPair;
        this.publicKey = keyPair.getPublic();
        this.caCert = caCert;
        this.fromDate = from;
        this.toDate = to;
    }

    @Deprecated
    public CertBuildContainer(X509v3CertificateBuilder x509v3CertificateBuilder, PublicKey publicKey, X509Certificate caCert, Date from, Date to) {
        this.x509v3CertificateBuilder = x509v3CertificateBuilder;
        this.publicKey = publicKey;
        this.caCert = caCert;
        this.fromDate = from;
        this.toDate = to;
    }

    @Deprecated
    public CertBuildContainer(X509v3CertificateBuilder x509v3CertificateBuilder, KeyPair keyPair, X509Certificate caCert) {
        this.x509v3CertificateBuilder = x509v3CertificateBuilder;
        this.keyPair = keyPair;
        this.caCert = caCert;
    }

    @Deprecated
    public CertBuildContainer(X509v3CertificateBuilder x509v3CertificateBuilder, PublicKey publicKey, X509Certificate caCert) {
        this.x509v3CertificateBuilder = x509v3CertificateBuilder;
        this.publicKey = publicKey;
        this.caCert = caCert;
    }
    //#endregion

    public PublicKey getPublicKey() {
        if (keyPair != null)
            return keyPair.getPublic();
        else
            return publicKey;
    }

}
