package caJava.fileManagement;

import caJava.Utils.MeUtils;
import caJava.core.cryptoAlg.CryptoAlg;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.AlgorithmNameFinder;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;


import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Logger;
/*
* конвертер в base64 и обратно в объекты
* */
public class CertEnveloper {
    static Logger logger = Logger.getLogger(CertEnveloper.class.getName());

    public static byte[] encodeCert(X509Certificate certificate) throws CertificateEncodingException {
        byte[] bytes = MeUtils.concatBytes(
                "-----BEGIN CERTIFICATE-----\n".getBytes(),
                Base64.getEncoder().encode(certificate.getEncoded()),
                "\n-----END CERTIFICATE-----\n".getBytes()
        );
        logger.info("encodeCert base64:\n" + new String(bytes));
        return bytes;
    }

    public static byte[] encodeCertRec(PKCS10CertificationRequest req) throws IOException {
        byte[] bytes = MeUtils.concatBytes(
                "-----BEGIN CERTIFICATE REQUEST-----\n".getBytes(),
                Base64.getEncoder().encode(req.getEncoded()),
                "\n-----END CERTIFICATE REQUEST-----\n".getBytes()
        );
        logger.info("encodeCertRec base64:\n" + new String(bytes));
        return bytes;
    }

    public static byte[] encodePrivateKey(PrivateKey privateKey) {
        byte[] bytes = MeUtils.concatBytes(
                "-----BEGIN PRIVATE KEY-----\n".getBytes(),
                Base64.getEncoder().encode(privateKey.getEncoded()),
                "\n-----END PRIVATE KEY-----\n".getBytes()
        );
        logger.info("encodePrivateKey base64");
        //logger.info("encodePrivateKey base64:\n"+new String(bytes));
        return bytes;
    }
    public static X509CertificateHolder decodeCertHolder(byte[] input) {
        try {
            //fixme нет ли более нормального способа?
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            InputStream in = new ByteArrayInputStream(input);
            return new X509CertificateHolder(factory.generateCertificate(in).getEncoded());
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static X509Certificate decodeCert(byte[] input) {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            InputStream in = new ByteArrayInputStream(input);
            return (X509Certificate) factory.generateCertificate(in);
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PKCS10CertificationRequest decodeCertRec(byte[] input) {

        return null;
    }
   static DefaultAlgorithmNameFinder defaultAlgorithmNameFinder = new DefaultAlgorithmNameFinder();
    public static PrivateKey decodePrivateKey(File privateKeyFile) throws IOException {
        try {
            PEMParser pemParser = new PEMParser(new FileReader(privateKeyFile));
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            KeyFactory factory = KeyFactory.getInstance(defaultAlgorithmNameFinder.getAlgorithmName(privateKeyInfo.getPrivateKeyAlgorithm()),"BC");
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded());
            return factory.generatePrivate(privKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return  null;
    }
    public static PrivateKey decodePrivateKey2(File privateKeyFile) throws IOException {
        //File privateKeyFile = new File("cer.pkey"); // private key file in PEM format
        PEMParser pemParser = new PEMParser(new FileReader(privateKeyFile));
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
        PrivateKey privateKey = null;
        try {
            privateKey = jcaPEMKeyConverter.getPrivateKey(object);
        } catch (PEMException e) {
            e.printStackTrace();
        }
        return privateKey;
        //todo decodePrivateKey (если надо)
        // https://stackoverflow.com/questions/22920131/read-an-encrypted-private-key-with-bouncycastle-spongycastle
    }


}
