import org.bouncycastle.operator.OperatorCreationException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

public class TestConverter {
    @Test
    public void derToPfx() {
        String[] args = {"-run", "convert", "-inFormat", "der", "-outFormat", "pfx", "-inFile", "exampleKeys/gost2001Ca.der",
                "-outFile", "exampleKeys/gost2001Ca.pfx", "-outPassword", "123456", "-alias", "gost2001Ca"};
        try {
            Main.main(args);
        } catch (Exception e) {
        }
    }
}
/*
*       String inFormat = params.get("inFormat");
        String outFormat = params.get("outFormat");
        String inFile = params.get("inFile");
        String inPrivateKey = params.getOrDefault("inPrivateKey", inFile + ".pkey");
        String outFile = params.get("outFile");
        String outPrivateKey = params.getOrDefault("outPrivateKey", outFile + ".pkey");
        String inPassword = params.get("outFile");
        String outPassword = params.get("outPassword");
        String alias = params.get("alias");
*
* */
