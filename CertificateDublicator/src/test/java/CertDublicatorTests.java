import caJava.Utils.MeUtils;
import caJava.core.CertAndKey;
import org.bouncycastle.operator.OperatorCreationException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

public class CertDublicatorTests {
    @Test
    public void testSub() throws Exception {
        MeUtils.loadBC();
        boolean isCa = false;
        String caCert = "../ConsoleApp/exampleKeys/gost2001Ca.der";
        String caPrivateKey = "../ConsoleApp/exampleKeys/gost2001Ca.der.pkey";
        String donorCertificate = "ocsp.der";
        String saveTo = "newCert.cer";
        CertAndKey certAndKey = CertDublicator.generateDublicate(caCert, caPrivateKey, donorCertificate);
        CertDublicator.saveToDer(certAndKey.getKeyPair(), certAndKey.getCertificate().getEncoded(), saveTo);
    }

    @Test
    public void testCa() throws Exception {
        MeUtils.loadBC();
        boolean isCa = true;
        //String caCert = "../ConsoleApp/exampleKeys/gost2001Ca.der";
       // String caPrivateKey = "../ConsoleApp/exampleKeys/gost2001Ca.der.pkey";
        String donorCertificate = "../ConsoleApp/exampleKeys/gost2001Ca.der";
        String saveTo = "newCertCa.cer";


        CertAndKey certAndKey = CertDublicator.generateDublicate(donorCertificate);
        CertDublicator.saveToDer(certAndKey.getKeyPair(), certAndKey.getCertificate().getEncoded(), saveTo);
    }
}
