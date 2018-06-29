import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.testng.annotations.Test;
import sun.security.provider.certpath.OCSP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

/*
 * Работает для ГОСТ только с JCP 2.0 ? О Ужас!!!
 * */
public class OCSPtest {

    /*
     * Read a certificate from the specified filepath.
     */
    private static X509Certificate getCertFromFile(String path) {
        X509Certificate cert = null;
        try {

            File certFile = new File(path);
            if (!certFile.canRead())
                throw new IOException(" File " + certFile.toString() +
                        " is unreadable");

            FileInputStream fis = new FileInputStream(path);
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            cert = (X509Certificate) cf.generateCertificate(fis);

        } catch (Exception e) {
            System.out.println("Can't construct X509 Certificate. " +
                    e.getMessage());
        }
        return cert;
    }

    private String getOcspUrl(X509Certificate certificate) throws Exception {
        byte[] octetBytes = certificate
                .getExtensionValue(Extension.authorityInfoAccess.getId());

        DLSequence dlSequence = null;
        ASN1Encodable asn1Encodable = null;

        try {
            ASN1Primitive fromExtensionValue = X509ExtensionUtil
                    .fromExtensionValue(octetBytes);
            if (!(fromExtensionValue instanceof DLSequence))
                return null;
            dlSequence = (DLSequence) fromExtensionValue;
            for (int i = 0; i < dlSequence.size(); i++) {
                asn1Encodable = dlSequence.getObjectAt(i);
                if (asn1Encodable instanceof DLSequence)
                    break;
            }
            if (!(asn1Encodable instanceof DLSequence))
                return null;
            dlSequence = (DLSequence) asn1Encodable;
            for (int i = 0; i < dlSequence.size(); i++) {
                asn1Encodable = dlSequence.getObjectAt(i);
                if (asn1Encodable instanceof DERTaggedObject)
                    break;
            }
            if (!(asn1Encodable instanceof DERTaggedObject))
                return null;
            DERTaggedObject derTaggedObject = (DERTaggedObject) asn1Encodable;
            byte[] encoded = derTaggedObject.getEncoded();
            if (derTaggedObject.getTagNo() == 6) {
                int len = encoded[1];
                return new String(encoded, 2, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void test() throws Exception {
        X509Certificate userCert = getCertFromFile("TestForOCSP.cer");
        X509Certificate caCert = getCertFromFile("subUcTestCryptoPro.der");
        OCSP.RevocationStatus ocsp = OCSP.check(userCert, caCert, URI.create("http://testca2012.cryptopro.ru/ocsp/ocsp.srf"), null, new Date());
       // OCSP.RevocationStatus ocsp = OCSP.check(userCert, caCert, URI.create(getOcspUrl(userCert)), null, new Date());
        System.out.println(ocsp);
    }
}
