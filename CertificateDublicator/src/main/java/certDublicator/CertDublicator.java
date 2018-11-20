package certDublicator;

import caJava.core.CertAndKey;
import caJava.core.creator.CertificateCreator;
import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.CryptoAlgFactory;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_512;
import caJava.core.cryptoAlg.impl.CryptoRSA;
import caJava.core.extensions.CertBuildContainer;
import caJava.core.extensions.ExtensionsHashMap;
import caJava.core.wrapper.ExtensionsMap;
import caJava.core.wrapper.SubjectMap;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class CertDublicator {
    static ExtensionsHashMap extensionsHashMap = new ExtensionsHashMap();


    public static String fromHex(String s) {
        byte bs[] = new byte[s.length() / 2];
        for (int i = 1; i < s.length(); i += 2) {
            bs[i / 2] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
        }
        try {
            return new String(bs, "UTF8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static X500Name getName(X509Certificate input) {
        X500Principal subjectX500Principal = input.getSubjectX500Principal();
        return new X500Name(subjectX500Principal.getName(X500Principal.RFC2253));
    }

    public static void parseCertName(X509Certificate input, File file) throws IOException {
        SubjectMap subjectMap = new SubjectMap();
        X500Principal subjectX500Principal = input.getSubjectX500Principal();
        X500Name x500Name = X500Name.getInstance(subjectX500Principal.getEncoded());
        subjectMap.save(x500Name, file);
    }

    public static Map<String, String> parseCertName(X509Certificate input) throws IOException {
        SubjectMap subjectMap = new SubjectMap();
        X500Principal subjectX500Principal = input.getSubjectX500Principal();
        X500Name x500Name = X500Name.getInstance(subjectX500Principal.getEncoded());
        return subjectMap.convert(x500Name);
    }

    public static void parseCertExtensions(X509Certificate input, CertBuildContainer buildContainer) throws IOException {
        for (String s : input.getNonCriticalExtensionOIDs()) {
            byte[] extensionValue = input.getExtensionValue(s);
            ASN1OctetString extOctetString = ASN1OctetString.getInstance(extensionValue);
            System.out.printf("%25s = %120s\n", s, fromHex(extOctetString.getLoadedObject().toString()));
            extensionAdder(buildContainer, s, extensionValue, false);
        }
        System.out.println();
        for (String s : input.getCriticalExtensionOIDs()) {
            byte[] extensionValue = input.getExtensionValue(s);

            ASN1OctetString extOctetString = ASN1OctetString.getInstance(extensionValue);
            System.out.printf("%25s =%120s\n", s, fromHex(extOctetString.getLoadedObject().toString()));
            extensionAdder(buildContainer, s, extensionValue, true);
        }


    }
    private static void extensionAdder(CertBuildContainer buildContainer, String s, byte[] extensionValue, boolean b) throws IOException {
        switch (s) {


            case "2.5.29.32":
                extensionsHashMap.get("certificatePolicies").apply(buildContainer, new String[]{"false","1.2.643.100.113.1"});
                //buildContainer.getX509v3CertificateBuilder().addExtension(new ASN1ObjectIdentifier(s), b, X509ExtensionUtil.fromExtensionValue(extensionValue));
                //FileManager.write(new File("2.5.29.35.cer"), Base64.getEncoder().encode(X509ExtensionUtil.fromExtensionValue(extensionValue).getEncoded()));
                break;
      /*      case "1.3.6.1.5.5.7.1.1":

                break;*/
            // case "2.5.29.35":
                //buildContainer.getX509v3CertificateBuilder().addExtension(new ASN1ObjectIdentifier(s), b, X509ExtensionUtil.fromExtensionValue(extensionValue));
                //FileManager.write(new File("2.5.29.35.cer"), Base64.getEncoder().encode(X509ExtensionUtil.fromExtensionValue(extensionValue).getEncoded()));
                //break;
            case "2.5.29.16":
                extensionsHashMap.get("privateKeyUsagePeriod").apply(buildContainer, null);
                break;
            case "2.5.29.14":
                extensionsHashMap.get("subjectKeyIdentifier").apply(buildContainer, null);
                break;
            default:
                buildContainer.getX509v3CertificateBuilder().addExtension(new ASN1ObjectIdentifier(s), b, X509ExtensionUtil.fromExtensionValue(extensionValue));
                break;
        }
    }

    public static CertAndKey generateDublicate( String donorFileName) throws Exception {
        return CertDublicator.generateDublicate(true, "", "", donorFileName);
    }

    public static CertAndKey generateDublicate(  String caFileName, String caPrivateKeyFileName,String donorFileName) throws Exception {
        return CertDublicator.generateDublicate(false, caFileName, caPrivateKeyFileName, donorFileName);
    }

    public static CertAndKey generateDublicate(boolean isCa, String caFileName, String caPrivateKeyFileName, String donorFileName) throws Exception {
        X509Certificate donorCert = CertEnveloper.decodeCert(FileManager.read(new File(donorFileName)));
        Map<String, String> x500donorSubjectName = parseCertName(donorCert);

        X500Name subject = SubjectMap.convert(x500donorSubjectName);
        // X500Name subject = getName(donorCert);
        X509Certificate ca = null;
        PrivateKey privateKeyCa = null;
        // create keypair
        Security.addProvider(new BouncyCastleProvider());
        CryptoAlg cryptoAlg = CryptoAlgGost2012_512.getCryptoAlg();
      //  CryptoAlg cryptoAlg = CryptoAlgGost2012_256.getCryptoAlg();
        //CryptoAlg cryptoAlg = CryptoAlgGost2001.getCryptoAlg();
       // CryptoAlg cryptoAlg = CryptoRSA.getCryptoAlg(2048);


        SecureRandom random = new SecureRandom();
        KeyPairGenerator keypairGen = KeyPairGenerator.getInstance(cryptoAlg.algorithm, cryptoAlg.cryptoProvider);
        keypairGen.initialize(new ECGenParameterSpec(cryptoAlg.ellipticCurve));
        KeyPair keypair = keypairGen.generateKeyPair();
        CryptoAlg cryptoAlgCA;
        if(isCa){
            privateKeyCa = keypair.getPrivate();
            cryptoAlgCA =cryptoAlg;
        }
        else {
            ca = CertEnveloper.decodeCert(FileManager.read(new File(caFileName)));
            privateKeyCa = CertEnveloper.decodePrivateKey(new File(caPrivateKeyFileName));
           cryptoAlgCA = CryptoAlgFactory.getInstance(ca.getSigAlgOID());
        }

        byte[] id = new byte[20];
        random.nextBytes(id);
        donorCert.getSerialNumber();
        BigInteger serial =  donorCert.getSerialNumber().add(new BigInteger("1000"));
       // BigInteger serial = new BigInteger(160, random);
        //Date date = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30);
        Date startDate = Date.from(LocalDate.of(2018, 9, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date endDate = Date.from(LocalDate.of(2019, 9, 1).atStartOfDay(ZoneOffset.UTC).toInstant());


        X500Name issuer;
        if (isCa)

            issuer = subject;
        else {
           issuer = new JcaX509CertificateHolder((X509Certificate) ca).getSubject();
           //issuer = new X500Name(ca.getSubjectX500Principal().getName(X500Principal.RFC2253));
        }
        X509v3CertificateBuilder x509v3CertificateBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                startDate,
                endDate,
                subject,
                keypair.getPublic());

        CertificateCreator certificateCreator = new CertificateCreator(cryptoAlgCA);

        CertBuildContainer buildContainer = new CertBuildContainer(x509v3CertificateBuilder, keypair, ca, startDate, endDate);
        parseCertExtensions(donorCert, buildContainer);

        CertAndKey certAndKey = certificateCreator.buildCertificate(privateKeyCa, keypair, buildContainer.getX509v3CertificateBuilder());
        return certAndKey;
    }

    public static void saveToDer(KeyPair keypair, byte[] serialized, String file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("-----BEGIN CERTIFICATE-----\n".getBytes());
            fos.write(Base64.getEncoder().encode(serialized));
            fos.write("\n-----END CERTIFICATE-----\n".getBytes());
        }
        try (FileOutputStream fos = new FileOutputStream(file + ".pkey")) {
            fos.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
            fos.write(Base64.getEncoder().encode(keypair.getPrivate().getEncoded()));
            fos.write("\n-----END PRIVATE KEY-----\n".getBytes());
        }
    }

}
