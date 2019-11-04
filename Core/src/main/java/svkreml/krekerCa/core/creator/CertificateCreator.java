package svkreml.krekerCa.core.creator;


import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlg;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;
import svkreml.krekerCa.core.extensions.CertBuildContainer;
import svkreml.krekerCa.core.extensions.ExtensionParam;
import svkreml.krekerCa.core.extensions.ExtensionsHashMap;
import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import svkreml.krekerCa.customOID.CustomBCStyle;
import svkreml.krekerCa.customOID.CustomExtension;
import svkreml.krekerCa.customOID.CustomText;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;


/*
 * Создание сертификатов
 * 1. самоподписанного корневого
 * 2. подписанного корневым
 * 3. из запроса
 * */
public class CertificateCreator {
    private static Logger logger = Logger.getLogger(CertificateCreator.class.getName());
    ExtensionsHashMap extensionsHashMap = new ExtensionsHashMap();
    SecureRandom random = new SecureRandom();
    private KeyPairGenerator keypairGen;
    private CryptoAlg cryptoAlg;
    private CryptoAlg cryptoAlgCA;

    public CertificateCreator(CryptoAlg cryptoAlg) throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        //fixme ТУТ КЛЮЧ должен определляться из caPKey, а не конструктора, если это не самоподписанный
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
            logger.info("Криптопровайдер BC был загружен");
        }
        this.cryptoAlg = cryptoAlg;
        this.cryptoAlgCA = cryptoAlg;
        keypairGen = KeyPairGenerator.getInstance(cryptoAlg.algorithm, cryptoAlg.cryptoProvider);
        if (cryptoAlg.ellipticCurve == null && cryptoAlg.keyLength != null)
            keypairGen.initialize(cryptoAlg.keyLength, new SecureRandom());
        else
            keypairGen.initialize(new ECGenParameterSpec(cryptoAlg.ellipticCurve));
    }



    public CertAndKey generateCertificateV2(X500Name subject, Vector<ExtensionObject> extensions, BigInteger serial, Date from, Date to) throws Exception {
        return generateCertificateV2(subject, extensions, serial, from, to, null, null);
    }

    public CertAndKey generateCertificateV2(X500Name subject, Vector<ExtensionObject> extensions, BigInteger serial, Date from, Date to, X509Certificate ca, PrivateKey caPKey) throws Exception {
        //fixme ТУТ КЛЮЧ должен определляться из caPKey, а не конструктора
        logger.info("Генерация сертификата\n\t" + subject + ", дата '" + from + "' - '" + to + "'");
        //Генерация пары ключей
        KeyPair keypair = keypairGen.generateKeyPair();


        X500Name issuer;
        if (ca != null) {
            issuer = new JcaX509CertificateHolder((X509Certificate) ca).getSubject();
            cryptoAlgCA = CryptoAlgFactory.getInstance(ca.getSigAlgOID());
            //issuer = new X500Name(ca.getSubjectX500Principal().getName(X500Principal.RFC2253));
        }
        else{
            issuer = subject;
        }
        //Создание заготовки под сертификат
        X509v3CertificateBuilder x509v3CertificateBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                from,
                to,
                subject,
                keypair.getPublic());

        //todo все расширения должны быть в каком-то конфиге, который на предыдущем этапе грузится из файла
        CertBuildContainer buildContainer = new CertBuildContainer(x509v3CertificateBuilder, keypair, ca, from, to);

        for (ExtensionObject extension : extensions) {
            extension.addExtension(buildContainer);
        }


        // extensionsGucRF(ca, keypair, x509v3CertificateBuilder);
        // build BouncyCastle certificate

        // if(cryptoAlg.signatureAlgorithm

        if (caPKey == null) caPKey = keypair.getPrivate();
        return buildCertificate(caPKey, keypair, x509v3CertificateBuilder);
    }


    @Deprecated
    public CertAndKey generateCertificate(X500Name subject, Vector<ExtensionParam> extensions, BigInteger serial, Date from, Date to) throws CertificateException, OperatorCreationException {
        return generateCertificate(subject, extensions, serial, from, to, null, null);
    }

    @Deprecated
    public CertAndKey generateCertificate(X500Name subject, Vector<ExtensionParam> extensions, BigInteger serial, Date from, Date to, X509Certificate ca, PrivateKey caPKey) throws CertificateException, OperatorCreationException {
        logger.info("Генерация сертификата\n\t" + subject + ", дата '" + from + "' - '" + to + "'");
        //Генерация пары ключей
        KeyPair keypair = keypairGen.generateKeyPair();


        X500Name issuer;
        if (ca != null)
            issuer = new JcaX509CertificateHolder((X509Certificate) ca).getSubject();
            //issuer = new X500Name(ca.getSubjectX500Principal().getName(X500Principal.RFC2253));
        else {
            issuer = subject;
        }
        //Создание заготовки под сертификат
        X509v3CertificateBuilder x509v3CertificateBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                from,
                to,
                subject,
                keypair.getPublic());

        //todo все расширения должны быть в каком-то конфиге, который на предыдущем этапе грузится из файла
        CertBuildContainer buildContainer = new CertBuildContainer(x509v3CertificateBuilder, keypair, ca);
        for (ExtensionParam extension : extensions) {
            extensionsHashMap.get(extension.name).apply(buildContainer, extension.params);
        }


        // extensionsGucRF(ca, keypair, x509v3CertificateBuilder);
        // build BouncyCastle certificate
        if (caPKey == null) caPKey = keypair.getPrivate();
        return buildCertificate(caPKey, keypair, x509v3CertificateBuilder);
    }

    public CertAndKey buildCertificate(PrivateKey caPKey, KeyPair keypair, X509v3CertificateBuilder x509v3CertificateBuilder) throws OperatorCreationException, CertificateException {

//fixme ТУТ КЛЮЧ должен определляться из caPKey, а не конструктора

        ContentSigner signer = new JcaContentSignerBuilder(cryptoAlgCA.getSignatureAlgorithm()).build(caPKey);
        X509CertificateHolder holder = x509v3CertificateBuilder.build(signer);

        // convert to JRE certificate
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
        //converter.setProvider(new BouncyCastleProvider());
        X509Certificate certificate = converter.getCertificate(holder);
        logger.info("Сертификат создан " + certificate);
        return new CertAndKey(keypair, certificate);
    }

    @Deprecated
    public CertAndKey createCert(LinkedHashMap<String, Object> name, Date startDate, Date endDate, X509Certificate caCert, PrivateKey caCertPrivateKey) throws CertificateException, IOException, OperatorCreationException, NoSuchAlgorithmException {
        logger.info("Генерация сертификата\n\t" + name + ", дата '" + startDate + "' - '" + endDate + "'");
        KeyPair keypair = keypairGen.generateKeyPair();
        X500Name subject = fillSubject(name);

        byte[] id = new byte[20];
        random.nextBytes(id);
        BigInteger serial = new BigInteger(160, random);
        //Date date = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30);
        // Date fromDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        // Date toDate = Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());


        X509v3CertificateBuilder certificate = new JcaX509v3CertificateBuilder(
                new X500Name(caCert.getSubjectX500Principal().getName(X500Principal.RFC2253)),
                serial,
                startDate,
                endDate,
                subject,
                keypair.getPublic());

        // Расширения сертификата
        addExtensionsToCertificate(caCert, keypair, certificate, null);


        // build BouncyCastle certificate
        return buildCertificate(caCertPrivateKey, keypair, certificate);
    }

    private void addExtensionsToCertificate(X509Certificate caCert, KeyPair keypair, X509v3CertificateBuilder x509v3CertificateBuilder, Object extensions) throws IOException, NoSuchAlgorithmException {
/*        //2.5.29.37 extKeyUsage: ['serverAuth', 'clientAuth', 'codeSigning', 'emailProtection']
        ExtendedKeyUsage usageEx = new ExtendedKeyUsage(new KeyPurposeId[]{KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_codeSigning, KeyPurposeId.id_kp_emailProtection});
        x509v3CertificateBuilder.addExtension(Extension.extendedKeyUsage, false, usageEx.getEncoded());*/

        /*2.5.29.14 Идентификатор ключа субъекта -- Открытый ключ субъекта*/
        x509v3CertificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keypair.getPublic()));

        /*Основные ограничения	2.5.29.19*/
        BasicConstraints constraints = new BasicConstraints(true);
        x509v3CertificateBuilder.addExtension(Extension.basicConstraints, true, constraints.getEncoded());

        addMimicExtensions(x509v3CertificateBuilder);

        addExtensionsAboutCa(x509v3CertificateBuilder, caCert);
    }


    @Deprecated
    private void addMimicExtensions(X509v3CertificateBuilder x509v3CertificateBuilder) throws IOException {
        /*1.2.643.100.112*/
        ASN1EncodableVector lines = new ASN1EncodableVector();
        lines.add(new DERUTF8String(CustomText.issuerSignTool_1));
        lines.add(new DERUTF8String(CustomText.issuerSignTool_2));
        lines.add(new DERUTF8String(CustomText.issuerSignTool_3));
        lines.add(new DERUTF8String(CustomText.issuerSignTool_4));
        x509v3CertificateBuilder.addExtension(CustomExtension.issuerSignTool, false, new BERSequence(lines));

        /*1.2.643.100.111*/
        x509v3CertificateBuilder.addExtension(CustomExtension.subjectSignTool, false, new DERUTF8String(CustomText.issuerSignTool_1));


        KeyUsage usage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.keyAgreement | KeyUsage.keyCertSign | KeyUsage.cRLSign);
        x509v3CertificateBuilder.addExtension(Extension.keyUsage, false, usage.getEncoded());

        /*  Certificate Policy:
        signToolClassKC1 (1.2.643.100.113.1)
        signToolClassKC2 (1.2.643.100.113.2)
        */
        ASN1EncodableVector policyExtensions = new ASN1EncodableVector();
        policyExtensions.add(new PolicyInformation(new ASN1ObjectIdentifier("1.2.643.100.113.1")));
        policyExtensions.add(new PolicyInformation(new ASN1ObjectIdentifier("1.2.643.100.113.2")));
        x509v3CertificateBuilder.addExtension(Extension.certificatePolicies, true, new DERSequence(policyExtensions));
    }


    // страница списка отзывов и о вышестоящем УЦ
    @Deprecated
    private void addExtensionsAboutCa(X509v3CertificateBuilder x509v3CertificateBuilder, X509Certificate caCert) throws CertIOException, NoSuchAlgorithmException {
        if (caCert == null) return; //корневому сертификату это не нужно
        //2.5.29.35 данные о вышестоящем сертификате
        GeneralName generalName = new GeneralName(new X500Name(caCert.getSubjectX500Principal().getName(X500Principal.RFC2253)));
        GeneralNames generalNames = new GeneralNames(generalName);
        x509v3CertificateBuilder.addExtension(Extension.authorityKeyIdentifier, false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(caCert.getPublicKey(), generalNames, caCert.getSerialNumber()));

        //2.5.29.31 – Список отзывов
        DistributionPointName distributionPoint = new DistributionPointName(new GeneralNames(new GeneralName(GeneralName.uniformResourceIdentifier, "http://10.216.0.72:81/revoked.crl")));
        DistributionPoint[] distPoints = new DistributionPoint[1];
        distPoints[0] = new DistributionPoint(distributionPoint, null, null);
        logger.info("добавление списка отзывов " + Extension.cRLDistributionPoints);
        x509v3CertificateBuilder.addExtension(Extension.cRLDistributionPoints, false, new CRLDistPoint(distPoints));
    }


    @Deprecated
    public CertAndKey createCertCa(LinkedHashMap<String, Object> name, Date startDate, Date endDate) throws CertificateException, IOException, OperatorCreationException, NoSuchAlgorithmException {
        logger.info("Генерация сертификата\n\t" + name + ", дата '" + startDate + "' - '" + endDate + "'");
        KeyPair keypair = keypairGen.generateKeyPair();

        X500Name subject = fillSubject(name);

        byte[] id = new byte[20];
        random.nextBytes(id);
        BigInteger serial = new BigInteger(160, random);
        //Date date = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30);
        // Date fromDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        // Date toDate = Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());

        X509v3CertificateBuilder certificate = new JcaX509v3CertificateBuilder(
                subject,
                serial,
                startDate,
                endDate,
                subject,
                keypair.getPublic());

        // Расширения сертификата
        addExtensions(keypair.getPublic(), subject, serial, certificate);

        // build BouncyCastle certificate
        ContentSigner signer = new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm).build(keypair.getPrivate());
        X509CertificateHolder holder = certificate.build(signer);

        // convert to JRE certificate
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
        converter.setProvider(new BouncyCastleProvider());
        logger.info("Сертификат создан " + certificate.toString());

        return new CertAndKey(converter.getCertificate(holder), keypair);
    }

    private void addExtensions(PublicKey publicKey, X500Name subject, BigInteger serial, X509v3CertificateBuilder certificate) throws IOException, NoSuchAlgorithmException {

        BasicConstraints constraints = new BasicConstraints(true);

        //extKeyUsage: ['serverAuth', 'clientAuth', 'codeSigning', 'emailProtection']
        ExtendedKeyUsage usageEx = new ExtendedKeyUsage(new KeyPurposeId[]{KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_codeSigning, KeyPurposeId.id_kp_emailProtection});
        certificate.addExtension(Extension.extendedKeyUsage, false, usageEx.getEncoded());

        certificate.addExtension(Extension.subjectKeyIdentifier, false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey));

        GeneralName generalName = new GeneralName(subject);
        GeneralNames generalNames = new GeneralNames(generalName);
        certificate.addExtension(Extension.authorityKeyIdentifier, false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(publicKey, generalNames, serial));

        certificate.addExtension(Extension.basicConstraints, true, constraints.getEncoded());

        addMimicExtensions(certificate);
    }

    private X500Name fillSubject(LinkedHashMap<String, Object> subject) {

        X500NameBuilder x500NameBld = new X500NameBuilder(CustomBCStyle.INSTANCE);

        String k = null;
        try {
            for (Map.Entry<String, Object> entry : subject.entrySet()) {
                k = entry.getKey();
                Object v = entry.getValue();
                if (v instanceof String)
                    x500NameBld.addRDN(CustomBCStyle.DefaultLookUp.get(k.toLowerCase()), (String) v);
                if (v instanceof ASN1Encodable)
                    x500NameBld.addRDN(CustomBCStyle.DefaultLookUp.get(k.toLowerCase()), (ASN1Encodable) v);
            }
        } catch (NullPointerException e) {
            throw new NullPointerException(k + " oid not found");
        }
        // во имя какой-то фигни и RFC2253 что-то где-то переворачивает порядок, поэтому надо делать в обратном порядке
        return x500NameBld.build();


    }


    //todo заполнить CertificateCreator
}
