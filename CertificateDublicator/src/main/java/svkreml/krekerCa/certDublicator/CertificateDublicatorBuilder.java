package svkreml.krekerCa.certDublicator;


import lombok.Builder;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.BCECGOST3410_2012PublicKey;
import org.bouncycastle.util.encoders.Hex;
import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.core.creator.CertificateCreator;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlg;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;
import svkreml.krekerCa.core.extensions.CertBuildContainer;
import svkreml.krekerCa.core.extensions.extParser.PrivateKeyUsagePeriodObject;
import svkreml.krekerCa.core.extensions.extParser.SubjectKeyIdentifierExtensionObject;
import svkreml.krekerCa.customOID.CustomBCStyle;
import svkreml.krekerCa.fileManagement.FileManager;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
class CertificateDublicatorBuilder {

    private static final int DEFAULT_CERT_VALIDITY_DAYS = 3650;
    private static Logger log = Logger.getLogger(FileManager.class.getName());
    //#region builder
    private CertAndKey caCertAndKey;
    private Date notBefore;
    private Date notAfter;
    private Date privateKeyFromDate;
    private Date privateKeyToDate;
    private CryptoAlg cryptoAlg;
    private BigInteger serialNumber;
    private X509Certificate donorCertificate;
    private PublicKey publicKeyForGenerate;
    private X500Name subjectForGenerate;

    @Builder
    public CertificateDublicatorBuilder(CertAndKey caCertAndKey, Date notBefore, Date notAfter, Date privateKeyFromDate, Date privateKeyToDate, CryptoAlg cryptoAlg, BigInteger serialNumber, X509Certificate donorCertificate, PublicKey publicKeyForGenerate, X500Name subjectForGenerate) {
        this.caCertAndKey = caCertAndKey;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.privateKeyFromDate = privateKeyFromDate;
        this.privateKeyToDate = privateKeyToDate;
        this.cryptoAlg = cryptoAlg;
        this.serialNumber = serialNumber;
        this.donorCertificate = donorCertificate;
        this.publicKeyForGenerate = publicKeyForGenerate;
        this.subjectForGenerate = subjectForGenerate;
    }
    //#endregion


    private static void parseCertExtensions(X509Certificate input, CertBuildContainer buildContainer) throws IOException, NoSuchAlgorithmException, BuildValidatorException, CertificateEncodingException {

        TBSCertificate tbsCertificate = TBSCertificate.getInstance(input.getTBSCertificate());
        final Extensions extensions = tbsCertificate.getExtensions();
        for (ASN1ObjectIdentifier extensionOID : extensions.getExtensionOIDs()) {
            String s = extensionOID.getId();
            boolean isCritical = input.getCriticalExtensionOIDs().contains(extensionOID.getId());
            if (input.getNonCriticalExtensionOIDs().contains(extensionOID.getId()) == isCritical) {
                throw new IOException("Расширение должно быть либо критичным, либо нет, но не быть сразу в двух или не в одном, невозможная ситуация");
            }
            {
                byte[] extensionValue = input.getExtensionValue(s);
                ASN1OctetString extOctetString = ASN1OctetString.getInstance(extensionValue);
                log.info(String.format(
                        "%25s = %120s\n",
                        s,
                        new String(Hex.decode(extOctetString.getLoadedObject().toString().replaceAll("#", "")), StandardCharsets.UTF_8)
                ));
                extensionAdder(buildContainer, s, extensionValue, isCritical);
            }
        }


    }

    private static void extensionAdder(CertBuildContainer buildContainer, String s, byte[] extensionValue, boolean b) throws IOException, BuildValidatorException, NoSuchAlgorithmException {
        /*
        FileManager.write(new File("2.5.29.35.cer"),
        Base64.getEncoder().encode(X509ExtensionUtil.fromExtensionValue(extensionValue).getEncoded()));
         // записать расширение в файл*/
        switch (s) {
            case "2.5.29.35"://authorityKeyIdentifier Идентификатор ключа центра сертификатов
                if (buildContainer.getCaCert() == null)
                    throw new BuildValidatorException("При копировании расширений сертификата" +
                            " было обнаружено расширение \"2.5.29.35:authorityKeyIdentifier: " +
                            "Идентификатор ключа центра сертификатов\", но сертификат выщестоящего" +
                            " УЦ предоставлен не был, продолжение невозможно.");

                GeneralName generalName = new GeneralName(X500Name.getInstance(CustomBCStyle.INSTANCE, buildContainer.getCaCert().getSubjectX500Principal().getEncoded()));
                GeneralNames generalNames = new GeneralNames(generalName);
                buildContainer.getX509v3CertificateBuilder().addExtension(Extension.authorityKeyIdentifier, false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(buildContainer.getCaCert().getPublicKey(), generalNames, buildContainer.getCaCert().getSerialNumber()));
                break;
            case "2.5.29.16": // PrivateKeyUsagePeriodObject.EXTENSION_IDENTIFIER_OID
                new PrivateKeyUsagePeriodObject().addExtension(buildContainer);
                break;
            case "2.5.29.14": // SubjectKeyIdentifierExtensionObject.EXTENSION_IDENTIFIER_OID
                new SubjectKeyIdentifierExtensionObject().addExtension(buildContainer);
                break;
            default:
                if (JcaX509ExtensionUtils.parseExtensionValue(extensionValue) == null) {
                    buildContainer.getX509v3CertificateBuilder().addExtension(
                            new ASN1ObjectIdentifier(s),
                            b,
                            DERNull.INSTANCE);
                } else {
                    buildContainer.getX509v3CertificateBuilder().addExtension(
                            new ASN1ObjectIdentifier(s),
                            b,
                            JcaX509ExtensionUtils.parseExtensionValue(extensionValue));
                }
                break;
        }
    }


    private void validateInput() throws BuildValidatorException, NoSuchAlgorithmException {
        log.info("***** Валидация входных данных *****");
        if (notBefore == null) {
            notBefore = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(notBefore);
            c.add(Calendar.DATE, -10); // вычтем 10 дней, чтобы было проще с криво настроеным временем
            notBefore = c.getTime();
        }
        log.info("Дата начала действия сертификата: " + notBefore);
        /*
         * если время окончания действия сертификата не указано, то делаем срок действия DEFAULT_CERT_VALIDITY_DAYS
         * */
        if (notAfter == null) {
            Calendar c = Calendar.getInstance();
            c.setTime(notBefore);
            c.add(Calendar.DATE, DEFAULT_CERT_VALIDITY_DAYS);
            notAfter = c.getTime();
        }
        log.info("Дата окончания действия сертификата: " + notAfter);
        if (privateKeyFromDate == null) {
            privateKeyFromDate = notBefore;
        }
        log.info("Дата начала действия закрытого ключа: " + privateKeyFromDate);
        if (privateKeyToDate == null) {
            privateKeyToDate = notAfter;
        }
        log.info("Дата окончания действия закрытого ключа: " + privateKeyToDate);

        if (donorCertificate == null) {
            log.severe("Не указан (null) 'donorCertificate' - сертификат, который нужно копировать");
            throw new BuildValidatorException("Не указан (null) 'donorCertificate' - сертификат, который нужно копировать");
        }

        if (cryptoAlg == null) {
            cryptoAlg = CryptoAlgFactory.getInstance(donorCertificate.getSigAlgName());
        }
        log.info("Используемый крипто-алгоритм: " + cryptoAlg);

        if (serialNumber == null) {
            donorCertificate.getSerialNumber();
            serialNumber = donorCertificate.getSerialNumber();
        }
        log.info("Серийный номер: " + serialNumber);
        log.info("***** Валидация входных данных завершена *****");
    }

    public CertAndKey copyCertificate() throws Exception {
        validateInput();

        return generateDublicate();
    }

    private CertAndKey generateDublicate() throws Exception {
        X500Name subject;
        if (subjectForGenerate != null) {
            subject = subjectForGenerate;
        } else {
            subject = new JcaX509CertificateHolder(donorCertificate).getSubject();
        }


        KeyPair keypair;
        if (publicKeyForGenerate != null) {
            keypair = new KeyPair(publicKeyForGenerate, null);
        } else {
            KeyPairGenerator keypairGen = KeyPairGenerator.getInstance(cryptoAlg.algorithm, cryptoAlg.cryptoProvider);
            if (cryptoAlg.ellipticCurve == null && cryptoAlg.keyLength != null)
                keypairGen.initialize(cryptoAlg.keyLength, new SecureRandom());
            else
                keypairGen.initialize(new ECGenParameterSpec(Objects.requireNonNull(cryptoAlg.ellipticCurve)));
            keypair = keypairGen.generateKeyPair();
            if(keypair.getPublic() instanceof BCECGOST3410_2012PublicKey  && ((BCECGOST3410_2012PublicKey) keypair.getPublic()).getGostParams().getDigestParamSet().getId().equals("1.2.643.2.2.30.1")){
                throw new Exception("Фатальная ошибка, при генерации ключа для ГОСТ 2012: обнаружен OID: 1.2.643.2.2.30.1 из ГОСТ 2001");
            }
        }



        CryptoAlg cryptoAlgCA;

        X500Name issuer;
        X509Certificate caX509Certificate = null;
        PrivateKey privateKeyCa;
        if (caCertAndKey == null) {
            if (publicKeyForGenerate != null)
                throw new BuildValidatorException("Невозможно создать самоподписанный сертификат с внешним публичным ключом, но без приватного");
            privateKeyCa = keypair.getPrivate();
            cryptoAlgCA = cryptoAlg;
            issuer = subject;
        } else {
            caX509Certificate = caCertAndKey.getCertificate();
            privateKeyCa = caCertAndKey.getPrivateKey();
            cryptoAlgCA = CryptoAlgFactory.getInstance(caX509Certificate.getSigAlgOID());
            issuer = new JcaX509CertificateHolder(caX509Certificate).getSubject();
        }

        X509v3CertificateBuilder x509v3CertificateBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serialNumber,
                notBefore,
                notAfter,
                subject,
                keypair.getPublic());

        CertificateCreator certificateCreator = new CertificateCreator(cryptoAlgCA);

        CertBuildContainer buildContainer = CertBuildContainer.builder()
                .x509v3CertificateBuilder(x509v3CertificateBuilder)
                .keyPair(keypair)
                .caCert(caX509Certificate)
                .fromDate(notBefore)
                .toDate(notAfter).build();
        parseCertExtensions(donorCertificate, buildContainer);

        return certificateCreator.buildCertificate(privateKeyCa, keypair, buildContainer.getX509v3CertificateBuilder());
    }
}
