package svkreml.krekerCa.certDublicator;

import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import picocli.CommandLine;
import svkreml.krekerCa.core.BcInit;
import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoAlgGost2001;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoAlgGost2012_512;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoRSA;
import svkreml.krekerCa.fileManagement.CertEnveloper;
import svkreml.krekerCa.fileManagement.FileManager;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "CertDublicator", mixinStandardHelpOptions = true, version = "checksum 1.0",
        description = "Создаёт новый сертификат, похожий на введённый")
public class CertDublicatorMain implements Callable<Integer> {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    @CommandLine.Option(names = {"-d", "--donorCert"}, description = "Сертификат для копирования")
    private String inputCertPath;

    @CommandLine.Option(names = {"-pub", "--donorPublicKey"}, description = "Опционально, Сертификат для копирования публичного ключа")
    private String donorPublicKeyPath;

    @CommandLine.Option(names = {"-n", "--donorSubject"}, description = "Опционально, Сертификат для копирования полей субъекта")
    private String donorSubject;


    @CommandLine.Option(names = {"-req", "--certificateRequest"}, description = "Опционально, запрос на сертификат, будет использоваться только открытый ключ из него")
    private String donorCertReq;

    @CommandLine.Option(names = {"-reqSub", "--certificateRequestSub"}, description = "Опционально, если значение данного параметра \"true\", то имя субъекта возьмётся из заявки")
    private String reqSub;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Файл, куда будет сохранён новый сертификат",
            defaultValue = "generatedCertificate.cer")
    private String outputNewCertPath;

    @CommandLine.Option(names = {"-c", "--caCert"}, description = "Опционально, Вышестоящий сертификат")
    private String caCertPath;

    @CommandLine.Option(names = {"-p", "--caCertPkey"},
            description = "Обязателен, если есть -с, --caCert, Закрытый ключ, которым сертификат будет подписан в формате pkcs8")
    private String caPkeyPath;

    @CommandLine.Option(names = {"-s", "--serial"}, description = "Опционально, Серийный номер генерируемого сертификата, hex")
    private String serial;

    @CommandLine.Option(names = {"-alg", "--pKeyAlg"},
            description = "Опционально, переопределение алгоритма приватного ключа: 2001, 2012, 2012str, rsa2048, rsa4096")
    private String pKeyAlg;

    @CommandLine.Option(names = {"-b", "--notBefore"}, description = "Опционально, Действителен с, dd/MM/yyyy HH:mm:ss")
    private String notBefore;
    @CommandLine.Option(names = {"-a", "--notAfter"}, description = "Опционально, Действителен по, dd/MM/yyyy HH:mm:ss")
    private String notAfter;

    public static void main(String... args) {
        int exitCode = new CommandLine(new CertDublicatorMain()).execute(args);
        System.exit(exitCode);
    }

    private static void buildAndWrite(String outputNewCertPath, CertificateDublicatorBuilder certificateDublicatorBuilder) throws Exception {
        CertAndKey createdCertificate = certificateDublicatorBuilder.copyCertificate();
        byte[] createdCertificateBytes = CertEnveloper.encodeCert(createdCertificate.getCertificate());
        FileManager.writeWithDir(new File(outputNewCertPath), createdCertificateBytes);
        if (createdCertificate.getPrivateKey() != null) {
            byte[] createdCertificatePrivateKeyBytes = CertEnveloper.encodePrivateKey(createdCertificate.getPrivateKey());
            FileManager.writeWithDir(new File(outputNewCertPath + ".pkey"), createdCertificatePrivateKeyBytes);
        }
    }

    private static BigInteger generateSerialNumber() {
        return BigInteger.valueOf(new Random().nextLong()).add(BigInteger.valueOf(Long.MAX_VALUE)).add(new BigInteger("7770000000000000000000", 16));
    }

    //#endregion

    @Override
    public Integer call() throws Exception {
        BcInit.init();
        if (inputCertPath == null) {
            System.out.println("Параметр --donorCert должен быть");
            System.exit(-1);
        }
        X509Certificate donorX509Certificate = CertEnveloper.decodeCert(FileManager.read(new File(inputCertPath)));
        CertificateDublicatorBuilder.CertificateDublicatorBuilderBuilder builder = CertificateDublicatorBuilder.builder();

        if (caCertPath != null) {
            CertAndKey caCertAndKey = new CertAndKey(
                    CertEnveloper.decodePrivateKey(FileManager.read(new File(caPkeyPath))),
                    CertEnveloper.decodeCert(FileManager.read(new File(caCertPath)))
            );
            builder.caCertAndKey(caCertAndKey);
        }

        if (serial != null) {
            builder.serialNumber(new BigInteger(serial, 16));
        } else {
            builder.serialNumber(generateSerialNumber());
        }

        if (notBefore != null) {
            builder.notBefore(dateFormatter.parse(notBefore));
        }

        if (donorPublicKeyPath != null && donorCertReq != null) {
            throw new Exception("Нельзя одновременно указывать -pub и -req");
        }
        if (donorPublicKeyPath != null) {
            X509Certificate certificate = CertEnveloper.decodeCert(FileManager.read(new File(donorPublicKeyPath)));
            builder.publicKeyForGenerate(Objects.requireNonNull(certificate).getPublicKey());
        }

        if (donorCertReq != null) {
            PKCS10CertificationRequest certificationRequest = null;
            try {
                certificationRequest = CertEnveloper.decodeCertRec(FileManager.read(new File(donorCertReq)));

                if (reqSub != null && reqSub.equals("true")) {
                    builder.subjectForGenerate(certificationRequest.getSubject());
                }

            } catch (IOException e) {
                throw new IOException("Не удалось прочитать заявку на сертификат, поддерживается только бинарный формат", e);
            }
            builder.publicKeyForGenerate(BouncyCastleProvider.getPublicKey(Objects.requireNonNull(certificationRequest).getSubjectPublicKeyInfo()));
        }

        if (donorSubject != null) {
            if(reqSub != null && reqSub.equals("true"))
                throw new Exception("Нельзя одновременно указывать -donorSubject и -reqSub");
            X509Certificate certificate = CertEnveloper.decodeCert(FileManager.read(new File(donorSubject)));
            builder.subjectForGenerate(
                    new JcaX509CertificateHolder(Objects.requireNonNull(certificate)).getSubject()
            );
        }

        if (notAfter != null) {
            builder.notAfter(dateFormatter.parse(notAfter));
        }

        if (pKeyAlg != null) {
            switch (pKeyAlg) {
                case "2001":
                    builder.cryptoAlg(CryptoAlgGost2001.getCryptoAlg());
                    break;
                case "2012":
                    builder.cryptoAlg(CryptoAlgGost2012_256.getCryptoAlg());
                    break;
                case "2012str":
                    builder.cryptoAlg(CryptoAlgGost2012_512.getCryptoAlg());
                    break;
                case "rsa2048":
                    builder.cryptoAlg(CryptoRSA.getCryptoAlg(2048));
                    break;
                case "rsa4096":
                    builder.cryptoAlg(CryptoRSA.getCryptoAlg(4096));
                    break;
                default:
                    throw new BuildValidatorException("неизвестный pKeyAlg");
            }
        }

        CertificateDublicatorBuilder certificateDublicatorBuilder = builder
                .donorCertificate(donorX509Certificate)
                .build();
        buildAndWrite(outputNewCertPath, certificateDublicatorBuilder);
        return 0;
    }
}
