import caJava.Utils.MeUtils;
import caJava.core.CertAndKey;
import certDublicator.CertDublicator;
import org.testng.annotations.Test;

public class CertDublicatorTests {
    @Test
    public void testSub() throws Exception {
        MeUtils.loadBC();
        String caCert = "C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\46230a85.000\\CA.cer";
        String caPrivateKey = "C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\46230a85.000\\CA.cer.pkey";
        String donorCertificate = "C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\cer.cer";
        String saveTo = "C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\copy.cer";
        CertAndKey certAndKey = CertDublicator.generateDublicate(caCert, caPrivateKey, donorCertificate);
        CertDublicator.saveToDer(certAndKey.getKeyPair(), certAndKey.getCertificate().getEncoded(), saveTo);
    }

    @Test
    public void testCa() throws Exception {
        MeUtils.loadBC();
        //String caCert = "../ConsoleApp/exampleKeys/gost2001Ca.der";
       // String caPrivateKey = "../ConsoleApp/exampleKeys/gost2001Ca.der.pkey";
        String donorCertificate = "../ConsoleApp/exampleKeys/gost2001Ca.der";
        String saveTo = "newCertCa.cer";
        CertAndKey certAndKey = CertDublicator.generateDublicate(donorCertificate);
        CertDublicator.saveToDer(certAndKey.getKeyPair(), certAndKey.getCertificate().getEncoded(), saveTo);
    }
    @Test
    public void testGuc() throws Exception {
        MeUtils.loadBC();
        //String caCert = "../ConsoleApp/exampleKeys/gost2001Ca.der";
        // String caPrivateKey = "../ConsoleApp/exampleKeys/gost2001Ca.der.pkey";
        String donorCertificate = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\donor\\guc.cer";
        String saveTo = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\copyGuc.cer";
        CertAndKey certAndKey = CertDublicator.generateDublicate(donorCertificate);
        CertDublicator.saveToDer(certAndKey.getKeyPair(), certAndKey.getCertificate().getEncoded(), saveTo);
    }
    @Test
    public void testVoskh() throws Exception {
        MeUtils.loadBC();
        String caCert = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\copyGuc.cer";
        String caPrivateKey = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\copyGuc.cer.pkey";
        String donorCertificate = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\donor\\voskh.cer";
        String saveTo = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\copyVoskh.cer";
        CertAndKey certAndKey = CertDublicator.generateDublicate(caCert, caPrivateKey, donorCertificate);
        CertDublicator.saveToDer(certAndKey.getKeyPair(), certAndKey.getCertificate().getEncoded(), saveTo);
    }
    @Test
    public void testTsp() throws Exception {
        MeUtils.loadBC();
        String caCert = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\copyVoskh.cer";
        String caPrivateKey = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\copyVoskh.cer.pkey";
        String donorCertificate = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\donor\\tsp.cer";
        String saveTo = "C:\\Users\\s.kremlev\\Desktop\\certs copy\\copyTsp.cer";
        CertAndKey certAndKey = CertDublicator.generateDublicate(caCert, caPrivateKey, donorCertificate);
        CertDublicator.saveToDer(certAndKey.getKeyPair(), certAndKey.getCertificate().getEncoded(), saveTo);
    }
}


//C:\Users\s.kremlev\Desktop\certs copy\donor\guc.cer