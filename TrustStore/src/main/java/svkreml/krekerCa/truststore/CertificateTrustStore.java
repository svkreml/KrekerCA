package svkreml.krekerCa.truststore;

import lombok.Getter;
import org.bouncycastle.asn1.x509.Extension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.X509CertImpl;
import svkreml.krekerCa.Utils.MeUtils;
import svkreml.krekerCa.core.BcInit;
import svkreml.krekerCa.fileManagement.CertEnveloper;
import svkreml.krekerCa.fileManagement.FileManager;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.*;
import java.util.*;


public class CertificateTrustStore {
    @Getter
    private String certStoreHomeFolder = "../certStore";
    private String crlStoreHomeFolder = "../crlStore";
    @Getter
    private Map<String, X509Certificate> certStore = new HashMap<>();
    @Getter
    private Map<String, X509CRL> crlStore = new HashMap<>();



    public void addCert(X509Certificate x509Certificate) throws IOException, CertificateEncodingException, NoSuchAlgorithmException {
        certStore.put(Base64.getEncoder().encodeToString(getSubjectKeyIdentifier(x509Certificate)), x509Certificate);
        FileManager.write(new File(certStoreHomeFolder + "/" + MeUtils.getThumbprint(x509Certificate)), x509Certificate.getEncoded());
    }

    public void addCrl(X509CRL x509CRL) throws IOException, CRLException {
        final Extension instance = Extension.getInstance(x509CRL.getExtensionValue(Extension.authorityKeyIdentifier.getId()));
        crlStore.put(instance.getExtnValue().toString(), x509CRL);
        FileManager.write(new File(certStoreHomeFolder + "/" + instance.getExtnValue().toString()), x509CRL.getEncoded());
    }

    public void readCertsFromDisk() throws IOException {
        for (final File fileEntry : Objects.requireNonNull(new File(certStoreHomeFolder).listFiles())) {
            final X509Certificate x509Certificate = CertEnveloper.decodeCert(FileManager.read(fileEntry));
            certStore.put(Base64.getEncoder().encodeToString(getSubjectKeyIdentifier(x509Certificate)), x509Certificate);
            System.out.println(fileEntry.getName());
        }
    }

    public int[] validateTrustStore(X509Certificate[] certificates, Date date) {
        int[] statuses = new int[certificates.length];
        for (int i = 0, certificatesLength = certificates.length; i < certificatesLength; i++) {
            X509Certificate certificate = certificates[i];

            try {
                certificate.checkValidity(date);
            } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                statuses[i] = 0x800B0101;
                continue;
            }

            try {
                certificate.verify(i < certificatesLength - 1 ? certificates[i + 1].getPublicKey() : certificate.getPublicKey());
            } catch (CertificateException | SignatureException | NoSuchProviderException | InvalidKeyException | NoSuchAlgorithmException e) {
                statuses[i] = 0x80096004;
                continue;
            }
            statuses[i] = 0;

            // todo CRL check // 7
        }
        return statuses;

    }

    public X509Certificate[] buildTrustStore(X509Certificate certificate) {


        final byte[] authKeyIdentifier = getAuthKeyIdentifier(certificate);
        final byte[] subjectKeyIdentifier = getSubjectKeyIdentifier(certificate);


        // проверка корневых сертификатов
        if (authKeyIdentifier == null || Arrays.equals(subjectKeyIdentifier, authKeyIdentifier)) {
            if (certStore.containsKey(Base64.getEncoder().encodeToString(subjectKeyIdentifier))
                    && certStore.get(Base64.getEncoder().encodeToString(subjectKeyIdentifier)).equals(certificate)) {
                return new X509Certificate[]{certificate};
            } else {
                return null;
            }
        }

        List<X509Certificate> chain = new ArrayList<>();
        chain.add(certificate);

        if (certStore.containsKey(Base64.getEncoder().encodeToString(authKeyIdentifier))) {
            final X509Certificate caCert = Objects.requireNonNull(certStore.get(Base64.getEncoder().encodeToString(authKeyIdentifier)));
            chain.add(caCert);
            recursiveAddToChain(chain, getAuthKeyIdentifier(caCert));
        }
        if (chain.size() == 1) return null;
        return chain.toArray(new X509Certificate[0]);
    }

    private byte[] getSubjectKeyIdentifier(X509Certificate certificate) {
        return ((X509CertImpl) certificate).getSubjectKeyId().getIdentifier();
    }

    private byte[] getAuthKeyIdentifier(X509Certificate certificate) {
        final KeyIdentifier authKeyId = ((X509CertImpl) certificate).getAuthKeyId();
        if (authKeyId == null) return null;
        return authKeyId.getIdentifier();
    }

    private void recursiveAddToChain(List<X509Certificate> chain, byte[] subjectKeyIdentifier) {
        if (subjectKeyIdentifier != null && certStore.containsKey(Base64.getEncoder().encodeToString(subjectKeyIdentifier))) {
            final X509Certificate caCert = certStore.get(Base64.getEncoder().encodeToString(subjectKeyIdentifier));
            chain.add(caCert);
            recursiveAddToChain(chain, getAuthKeyIdentifier(caCert));
        }
    }
}

