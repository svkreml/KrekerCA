package sandbox;

import caJava.Utils.MeUtils;
import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

public class CreateCertChain {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, CertificateEncodingException, SignatureException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
            //logger.info("Криптопровайдер BC был загружен");
        }
        CryptoAlg cryptoAlg = CryptoAlgGost2001.getCryptoAlg();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(cryptoAlg.algorithm, cryptoAlg.cryptoProvider);
        keyPairGenerator.initialize(new ECGenParameterSpec(cryptoAlg.ellipticCurve));


        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Date startDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date endDate = Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal dnName = new X500Principal("CN=Sergey");
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setSubjectDN(dnName);


        byte[] cerCa = FileManager.read(new File("cer.der"));
        X509Certificate caCert = CertEnveloper.decodeCert(cerCa);


        File privateKeyFile = new File("cer.pkey"); // private key file in PEM format

        PrivateKey caCertPrivateKey = CertEnveloper.decodePrivateKey(privateKeyFile);


        certGen.setIssuerDN(caCert.getSubjectX500Principal());
        certGen.setNotBefore( startDate);
        certGen.setNotAfter(endDate);
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm(cryptoAlg.signatureAlgorithm);

        certGen.addExtension(Extension.authorityKeyIdentifier, false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier( caCert.getPublicKey(), caCert.getIssuerX500Principal(), caCert.getSerialNumber()));
        certGen.addExtension(Extension.subjectKeyIdentifier, false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(caCert.getPublicKey()));



        X509Certificate cert = certGen.generate(caCertPrivateKey, "BC");

        FileManager.write(new File("cer_sub.der"), MeUtils.concatBytes(CertEnveloper.encodeCert(cert), CertEnveloper.encodePrivateKey(keyPair.getPrivate())));

    }
}
