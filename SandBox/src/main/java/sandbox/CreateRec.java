package sandbox;

import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.customOID.CustomBCStyle;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNumericString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class CreateRec {
    public static void main(String[] args) throws IOException, OperatorCreationException, CertificateException, InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        Security.addProvider(new BouncyCastleProvider());

        CryptoAlg cryptoAlg = CryptoAlgGost2001.getCryptoAlg();

        //   SecureRandom random = new SecureRandom();

// create keypair
        KeyPairGenerator keypairGen = KeyPairGenerator.getInstance(cryptoAlg.algorithm, cryptoAlg.cryptoProvider);
        keypairGen.initialize(new ECGenParameterSpec(cryptoAlg.ellipticCurve));
        KeyPair keypair = keypairGen.generateKeyPair();

// fill in certificate fields
        X500Name subject = new X500NameBuilder(CustomBCStyle.INSTANCE)
                .addRDN(CustomBCStyle.CN, "НИИ \"Крекер\"")
                .addRDN(CustomBCStyle.SURNAME, "Иванов")
                .addRDN(CustomBCStyle.GIVENNAME, "Иван Иванович")
                .addRDN(CustomBCStyle.T, "Начальник отдела")
                .addRDN(CustomBCStyle.O, "НИИ \"Крекер\"")
                .addRDN(CustomBCStyle.L, ("г. Москва"))
                .addRDN(CustomBCStyle.ST, "77 Москва")
                .addRDN(CustomBCStyle.C, "RU")
                .addRDN(CustomBCStyle.EmailAddress, "mail@test.ru")
                .addRDN(CustomBCStyle.STREET, "улица Улица, дом 84")
                .addRDN(CustomBCStyle.ИНН, new DERNumericString("0012345678"))
                .addRDN(CustomBCStyle.ОГРН, new DERNumericString("0012345678"))
                //.addRDN(CustomBCStyle.СНИЛС, new DERNumericString("0012345678"))
                .build();


        PKCS10CertificationRequestBuilder requestBuilder = new JcaPKCS10CertificationRequestBuilder(subject, keypair.getPublic());

        ExtensionsGenerator extGen = new ExtensionsGenerator();

        String[] params = new String[]{"true", "true"};
        BasicConstraints constraints;
        if (params[1].contains("e"))
            constraints = new BasicConstraints(Boolean.valueOf(params[1]));
        else
            constraints = new BasicConstraints(Integer.parseInt(params[1]));
        extGen.addExtension(Extension.basicConstraints, Boolean.valueOf(((String[]) params)[0]), constraints.getEncoded());

        params = new String[]{"1.2.643.100.113.1", "1.2.643.100.113.2", "1.2.643.100.113.3", "1.2.643.100.113.4"};
        ASN1EncodableVector policyExtensions = new ASN1EncodableVector();
        for (int i = 1; i < params.length; i++) {
            policyExtensions.add(new PolicyInformation(new ASN1ObjectIdentifier(params[i])));
        }
        extGen.addExtension(Extension.certificatePolicies, false, new DERSequence(policyExtensions));

        requestBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());


        /*
        *  ExtensionsGenerator extGen = new ExtensionsGenerator();

            GeneralNames subjectAltNames = new GeneralNames(namesList.toArray(new GeneralName [] {}));
            extGen.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
            builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());*/






        PKCS10CertificationRequest req1 = requestBuilder.build(new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm).setProvider(cryptoAlg.cryptoProvider).build(keypair.getPrivate()));
        //   JcaPKCS10CertificationRequest req2 = new JcaPKCS10CertificationRequest(req1.getEncoded()).setProvider("BC");


        try (FileOutputStream fos = new FileOutputStream("rec1.pkcs10")) {
            saveToDer(req1, fos);
        }
        try (FileOutputStream fos = new FileOutputStream("rec1.rec")) {
            saveToCer(req1, fos);
        }
        try (FileOutputStream fos = new FileOutputStream("rec1.pkey")) {
            saveToDer(keypair, fos);
        }

//        try (FileOutputStream fos = new FileOutputStream("rec2.pkcs10")) {
//            saveToDer(keypair, req2, fos);
//        }

    }

    private static void saveToDer(PKCS10CertificationRequest req1, FileOutputStream fos) throws IOException {
        fos.write("-----BEGIN CERTIFICATE REQUEST-----\n".getBytes());
        fos.write(Base64.getEncoder().encode(req1.getEncoded()));
        fos.write("\n-----END CERTIFICATE REQUEST-----\n".getBytes());
    }
    private static void saveToCer(PKCS10CertificationRequest req1, FileOutputStream fos) throws IOException {
        //fos.write("-----BEGIN CERTIFICATE REQUEST-----\n".getBytes());
        fos.write(req1.getEncoded());
        //fos.write("\n-----END CERTIFICATE REQUEST-----\n".getBytes());
    }

    private static void saveToDer(KeyPair keypair, FileOutputStream fos) throws IOException {
        fos.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
        fos.write(Base64.getEncoder().encode(keypair.getPrivate().getEncoded()));
        fos.write("\n-----END PRIVATE KEY-----\n".getBytes());
    }


}

