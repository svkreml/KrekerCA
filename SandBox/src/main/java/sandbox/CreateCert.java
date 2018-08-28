package sandbox;

import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.customOID.*;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;

public class CreateCert {
    public static void main(String[] args) throws NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, OperatorCreationException, InvalidAlgorithmParameterException {
        Security.addProvider(new BouncyCastleProvider());
        CryptoAlg cryptoAlg = CryptoAlgGost2001.getCryptoAlg();

        SecureRandom random = new SecureRandom();
// create keypair
        KeyPairGenerator keypairGen = KeyPairGenerator.getInstance(cryptoAlg.algorithm, cryptoAlg.cryptoProvider);
        keypairGen.initialize(new ECGenParameterSpec(cryptoAlg.ellipticCurve));
        KeyPair keypair = keypairGen.generateKeyPair();
// fill in certificate fields
        X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);

        // во имя какой-то фигни и RFC2253 что-то где-то переворачивает порядок, поэтому надо делать в обратном порядке
        x500NameBld.addRDN(CustomBCStyle.СНИЛС, new DERNumericString("0012345678"));
        x500NameBld.addRDN(CustomBCStyle.ОГРН, new DERNumericString("0012345678"));
        x500NameBld.addRDN(CustomBCStyle.ИНН, new DERNumericString("0012345678"));
        x500NameBld.addRDN(BCStyle.STREET, "улица Улица, дом 84");
        x500NameBld.addRDN(BCStyle.EmailAddress, "mail@test.ru");
        x500NameBld.addRDN(BCStyle.C, "RU");
        x500NameBld.addRDN(BCStyle.ST, "77 Москва");
        x500NameBld.addRDN(BCStyle.L, ("г. Москва"));
        x500NameBld.addRDN(BCStyle.O, "НИИ \"Крекер\"");
        x500NameBld.addRDN(BCStyle.T, "Начальник отдела");
        x500NameBld.addRDN(BCStyle.GIVENNAME, "Иван Иванович");
        x500NameBld.addRDN(BCStyle.SURNAME, "Иванов");
        x500NameBld.addRDN(BCStyle.CN, "НИИ \"Крекер\"");
        X500Name subject = x500NameBld.build();
        System.out.println("subject = " + subject);

        byte[] id = new byte[20];
        random.nextBytes(id);
        BigInteger serial = new BigInteger(160, random);
        //Date date = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30);
        Date startDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date endDate = Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());


        X509v3CertificateBuilder certificate = new JcaX509v3CertificateBuilder(
                subject,
                serial,
                startDate,
                endDate,
                subject,
                keypair.getPublic());


        // Расширения сертификата
        BasicConstraints constraints = new BasicConstraints(true);

        //extKeyUsage: ['serverAuth', 'clientAuth', 'codeSigning', 'emailProtection']
        ExtendedKeyUsage usageEx = new ExtendedKeyUsage(new KeyPurposeId[]{KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_codeSigning, KeyPurposeId.id_kp_emailProtection});
        certificate.addExtension(Extension.extendedKeyUsage, false, usageEx.getEncoded());

        certificate.addExtension(Extension.subjectKeyIdentifier, false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keypair.getPublic()));

        GeneralName generalName = new GeneralName(subject);
        GeneralNames generalNames = new GeneralNames(generalName);
        certificate.addExtension(Extension.authorityKeyIdentifier, false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(keypair.getPublic(), generalNames, serial));

        certificate.addExtension(Extension.basicConstraints, true, constraints.getEncoded());
/*1.2.643.100.112*/

        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new DERUTF8String(CustomText.issuerSignTool_1));
        v.add(new DERUTF8String(CustomText.issuerSignTool_2));
        v.add(new DERUTF8String(CustomText.issuerSignTool_3));
        v.add(new DERUTF8String(CustomText.issuerSignTool_4));
        certificate.addExtension(CustomExtension.issuerSignTool, false, new BERSequence(v));
/*
         SEQUENCE {
         OBJECT IDENTIFIER subjectSignTool (1.2.643.100.111)
         OCTET STRING, encapsulates {
         UTF8String "КриптоПро CSP" (версия 3.9)"
         }
        }
*/

        certificate.addExtension(CustomExtension.subjectSignTool, false, new DERUTF8String(CustomText.issuerSignTool_1));


        KeyUsage usage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.keyAgreement | KeyUsage.keyCertSign | KeyUsage.cRLSign);
        certificate.addExtension(Extension.keyUsage, false, usage.getEncoded());



/*  [1]Certificate Policy:
                Policy Identifier=1.2.643.100.113.1
            [2]Certificate Policy:
                Policy Identifier=1.2.643.100.113.2*/


        ASN1EncodableVector vv = new ASN1EncodableVector();
        vv.add(new PolicyInformation(new ASN1ObjectIdentifier("1.2.643.100.113.1")));
        vv.add(new PolicyInformation(new ASN1ObjectIdentifier("1.2.643.100.113.2")));
        certificate.addExtension(Extension.certificatePolicies, true, new DERSequence(vv));


// build BouncyCastle certificate
        System.out.println(cryptoAlg.signatureAlgorithm);
        ContentSigner signer = new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm)
                .build(keypair.getPrivate());
        X509CertificateHolder holder = certificate.build(signer);

// convert to JRE certificate
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
        converter.setProvider(new BouncyCastleProvider());
        X509Certificate x509 = converter.getCertificate(holder);
// serialize in DER format
        byte[] serialized = x509.getEncoded();
        saveToDer(keypair, serialized);
/*        try (FileOutputStream fos = new FileOutputStream("cert1.pkey")) {
            fos.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
            fos.write(Base64.getEncoder().encode(keypair.getPrivate().getEncoded()));
            fos.write("-----END PRIVATE KEY-----\n".getBytes());
        }*/

    }

    private static void saveToDer(KeyPair keypair, byte[] serialized) throws IOException {
        try (FileOutputStream fos = new FileOutputStream("cert5.der")) {
            fos.write("-----BEGIN CERTIFICATE-----\n".getBytes());
            fos.write(Base64.getEncoder().encode(serialized));
            fos.write("\n-----END CERTIFICATE-----\n".getBytes());
            fos.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
            fos.write(Base64.getEncoder().encode(keypair.getPrivate().getEncoded()));
            fos.write("\n-----END PRIVATE KEY-----\n".getBytes());
        }
    }

}
