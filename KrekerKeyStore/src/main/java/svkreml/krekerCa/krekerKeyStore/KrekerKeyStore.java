package svkreml.krekerCa.krekerKeyStore;

import lombok.Getter;
import svkreml.krekerCa.Utils.MeUtils;
import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.fileManagement.CertEnveloper;
import svkreml.krekerCa.fileManagement.FileManager;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KrekerKeyStore {
   private Map<String, CertAndKey> keyStore;

    public CertAndKey get(String sha1){
        return keyStore.get(sha1);
    }
    public boolean containsKey(String sha1){
        return keyStore.containsKey(sha1);
    }

    private String testKeysStoreHomeFolder = "../keyStore";

    public KrekerKeyStore() {
        keyStore = new HashMap<>();
    }


    public void readKeysFromDisk() throws CertificateEncodingException, NoSuchAlgorithmException, IOException {
        readKeysFromDisk(new File(testKeysStoreHomeFolder));
    }

    public void readKeysFromDisk(File folder) throws IOException, CertificateEncodingException, NoSuchAlgorithmException {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.getName().toLowerCase().endsWith("pkey")) {
                final X509Certificate x509Certificate = CertEnveloper.decodeCert(FileManager.read(fileEntry));
                final PrivateKey privateKey = CertEnveloper.decodePrivateKey(FileManager.read(new File(fileEntry.getAbsolutePath() + ".pkey")));
                keyStore.put(MeUtils.getThumbprint(Objects.requireNonNull(x509Certificate)), new CertAndKey(privateKey, x509Certificate));
                System.out.println(fileEntry.getName());
            }
        }
    }
}
