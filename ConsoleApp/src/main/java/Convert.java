import caJava.core.CertAndKey;
import caJava.core.pfx.PfxUtils;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;

public class Convert {
    public static void run(LinkedHashMap<String, String> params) throws IOException, KeyStoreException, CertificateEncodingException {
        String inFormat = params.get("inFormat");
        String outFormat = params.get("outFormat");
        String inFile = params.get("inFile");
        String inPrivateKey = params.getOrDefault("inPrivateKey", inFile + ".pkey");
        String outFile = params.get("outFile");
        String outPrivateKey = params.getOrDefault("outPrivateKey", outFile + ".pkey");
        String inPassword = params.get("outFile");
        String outPassword = params.get("outPassword");
        String alias = params.get("alias");

        CertAndKey certAndKey = null;
        //case in format
        switch (inFormat) {
            case "pfx":
                certAndKey = PfxUtils.convertToCertAndKey(new File(inFile), inPassword);
                break;
            case "der":
                X509Certificate certificate = CertEnveloper.decodeCert(FileManager.read(new File(inFile)));
                PrivateKey privateKey = CertEnveloper.decodePrivateKey(new File(inPrivateKey));
                certAndKey = new CertAndKey(privateKey, certificate);
                break;
            default:
                System.out.println("unknown");
                System.exit(1);
        }

        switch (outFormat) {
            case "pfx":
                PfxUtils.convertToPfx(certAndKey, alias, outPassword, new File(outFile));
                break;
            case "der":
                FileManager.write(new File(outFile),CertEnveloper.encodeCert(certAndKey.getCertificate()));
                FileManager.write(new File(outPrivateKey),CertEnveloper.encodePrivateKey(certAndKey.getPrivateKey()));
                break;
            default:
                System.out.println("unknown");
                System.exit(1);
        }
        //case out format
    }
}
