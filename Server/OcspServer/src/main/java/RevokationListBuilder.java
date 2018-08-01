import caJava.core.BcInit;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class RevokationListBuilder {

    static DigestCalculator digestCalculator;

    public RevokationListBuilder() {
        try {
            BcInit.init();
            JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
            DigestCalculatorProvider digestCalculatorProvider = digestCalculatorProviderBuilder.build();
            digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }
    }


    List<RevokedCertificate> getTestList() {
        try {
            List<RevokedCertificate> list = new ArrayList<>();

            //read CA certificate
            File caFile = new File("Server/OcspServer/ca.der");
            byte[] bytes = FileManager.read(caFile);
            X509CertificateHolder caCertHolder = CertEnveloper.decodeCertHolder(bytes);

            //read revoked certificate
            File revoked = new File("Server/OcspServer/testSubOCSP.der");
            bytes = FileManager.read(revoked);
            X509Certificate revokedCert = CertEnveloper.decodeCert(bytes);
            BigInteger revokedSerialNumber = revokedCert.getSerialNumber();

            //put revoked cert in array
            CertificateID revokedID = new CertificateID(digestCalculator, caCertHolder, revokedSerialNumber);
            Date date = new GregorianCalendar(2018, 10, 1).getTime();
            RevokedCertificate revokedCertificate = new RevokedCertificate(revokedID, date);
            list.add(revokedCertificate);

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OCSPException e) {
            e.printStackTrace();
        }
        return null;
    }
}
