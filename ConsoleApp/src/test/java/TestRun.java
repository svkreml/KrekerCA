import org.bouncycastle.operator.OperatorCreationException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TestRun {
    @Test
    public void test1(){
        Date date = Date.from(LocalDate.parse("2012-12-24",DateTimeFormatter.ISO_DATE).atStartOfDay(ZoneOffset.UTC).toInstant());
        System.out.println(date);
    }
    @Test
    public void createCa(){
        String[] args = {"-run", "createCert", "-subject", "subjectCa.json", "-extensions", "file", "-extensions",
                "extensionsCa.json", "-outputDer", "gost2001Ca.der", "-pass", "123456", "-alg", "gost2001","-dateFrom","2012-12-24","-dateTo","2024-12-24"};
        try {
            Main.main(args);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
    @Test
    public void createCert(){
        String[] args = {"-run", "createCert", "-subject", "subject.json", "-extensions", "file", "-extensions", "extensions.json", "-outputDer",
                "testSub.der", "-pass", "123456", "-alg", "rsa2048","-dateFrom","2012-12-24","-dateTo","2024-12-24",
                "-ca","-caFile", "gost2001Ca.der","-caPKey","gost2001Ca.der.pkey"};
        try {
            Main.main(args);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void createCertTsa(){
        String[] args = {"-run", "createCert", "-subject", "subject.json", "-extensions", "file", "-extensions", "extensionsTsa.json", "-outputDer",
                "testTsa.der", "-pass", "123456", "-alg", "gost2001","-dateFrom","2012-12-24","-dateTo","2024-12-24",
                "-ca","-caFile", "exampleKeys/gost2001Ca.der","-caPKey","exampleKeys/gost2001Ca.der.pkey"};
        try {
            Main.main(args);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void createCertPfx(){
        String[] args = {"-run", "createCert", "-subject", "subject.json", "-extensions", "file", "-extensions", "extensions.json", "-outputDer",
                "testSub.der", "-pass", "123456", "-alg", "rsa2048","-dateFrom","2012-12-24","-dateTo","2024-12-24",
                "-ca","-caFile", "gost2001Ca.der","-caPKey","gost2001Ca.der.pkey","-pfx", "data.pfx"};
        try {
            Main.main(args);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
    @Test
    public void createCertOcsp(){
        String[] args = {"-run", "createCert", "-subject", "subject.json", "-extensions", "file", "-extensions", "extensionsOCSP.json", "-outputDer",
                "testSubOCSP2.der", "-pass", "123456", "-alg", "rsa2048","-dateFrom","2012-12-24","-dateTo","2024-12-24",
                "-ca","-caFile", "Ca.der","-caPKey","Ca.der.pkey"};
        try {
            Main.main(args);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
    @Test
    public void createCertOcspAnother(){
        String[] args = {"-run", "createCert", "-subject", "subject.json", "-extensions", "file", "-extensions", "extensionsOCSP.json", "-outputDer",
                "testSubOCSP.der", "-pass", "123456", "-alg", "rsa2048","-dateFrom","2012-12-24","-dateTo","2024-12-24",
                "-ca","-caFile", "gost2001Ca.der","-caPKey","gost2001Ca.der.pkey"};
        try {
            Main.main(args);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
}
