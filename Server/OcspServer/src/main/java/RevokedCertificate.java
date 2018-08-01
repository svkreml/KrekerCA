import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.operator.DigestCalculator;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Date;

public class RevokedCertificate {
    private CertificateID certificateID;
    private Date date;
    private int reason;

    public RevokedCertificate(X509CertificateHolder revokedCert, Date date, int reason, DigestCalculator digestCalculator, X509CertificateHolder caCertificateHolder) throws OCSPException {
        this.certificateID = new CertificateID(digestCalculator, caCertificateHolder, revokedCert.getSerialNumber());
        this.date = date;
        this.reason = reason;
    }

    public RevokedCertificate(X509Certificate revokedCert, Date date, int reason, DigestCalculator digestCalculator, X509CertificateHolder caCertificateHolder) throws OCSPException {
        this.certificateID = new CertificateID(digestCalculator, caCertificateHolder, revokedCert.getSerialNumber());
        this.date = date;
        this.reason = reason;
    }

    public RevokedCertificate(BigInteger revokedSerialNumber, Date date, int reason, DigestCalculator digestCalculator, X509CertificateHolder caCertificateHolder) throws OCSPException {
        this.certificateID = new CertificateID(digestCalculator, caCertificateHolder, revokedSerialNumber);
        this.date = date;
        this.reason = reason;
    }


    public RevokedCertificate(CertificateID certificateID, Date date, int reason) {
        this.certificateID = certificateID;
        this.date = date;
        this.reason = reason;
    }

    public RevokedCertificate(CertificateID certificateID, Date date) {
        this.certificateID = certificateID;
        this.date = date;
        this.reason = CRLReason.unspecified;
    }

    public RevokedCertificate(CertificateID certificateID, int reason) {
        this.certificateID = certificateID;
        this.date = new Date();
        this.reason = reason;
    }

    public RevokedCertificate(CertificateID certificateID) {
        this.certificateID = certificateID;
        this.date = new Date();
        this.reason =  CRLReason.unspecified;
    }

    public CertificateID getCertificateID() {
        return certificateID;
    }

    public RevokedStatus getRevokedStatus() {
        return new RevokedStatus(date, reason);
    }


}
