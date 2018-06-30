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
                "extensionsCa.json", "-outputDer", "test.der", "-pass", "123456", "-alg", "rsa4096","-dateFrom","2012-12-24","-dateTo","2024-12-24"};
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
                "-ca","-caFile", "test.der","-caPKey","test.der.pkey"};
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
                "testSubOCSP.der", "-pass", "123456", "-alg", "rsa2048","-dateFrom","2012-12-24","-dateTo","2024-12-24",
                "-ca","-caFile", "test.der","-caPKey","test.der.pkey"};
        try {
            Main.main(args);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
}
