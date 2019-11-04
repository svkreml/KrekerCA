package svkreml.krekerCA;

import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.core.creator.CertificateCreator;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlg;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoAlgGost2001;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoAlgGost2012_512;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoRSA;
import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import svkreml.krekerCa.customOID.CustomBCStyle;
import svkreml.krekerCa.fileManagement.CertEnveloper;
import svkreml.krekerCa.fileManagement.FileManager;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.slf4j.Logger;
import svkreml.krekerCA.gui.params.extensions.ExtensionField;
import svkreml.krekerCA.gui.params.subject.SubjectField;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class CertificateGeneratorHandler {


    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CertificateGeneratorHandler.class);

    private static String getThumbprint(X509Certificate cert)
            throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        String digestHex = DatatypeConverter.printHexBinary(digest);
        return digestHex.toLowerCase();
    }

    void loadCertificate(String path, TextField serialTF, DatePicker dateFromTF, DatePicker dateToTF, ChoiceBox<String> alg, Vector<SubjectField> subjectFields, Vector<ExtensionField> extensionFields, CheckBox selfSigned, TextField caCertificateTF) throws IOException, CertificateEncodingException, NoSuchAlgorithmException {
        //загрузим сертификат из файла
        X509Certificate donorCert = CertEnveloper.decodeCert(FileManager.read(new File(path)));

        X509Certificate caCert = null;
        if (selfSigned.isSelected())
            caCert = CertEnveloper.decodeCert(FileManager.read(new File(caCertificateTF.getText())));


        // проставим серийный номер
        serialTF.setText(donorCert.getSerialNumber().toString(10));
        dateFromTF.setValue(convertToLocalDateViaMilisecond(donorCert.getNotBefore()));
        dateToTF.setValue(convertToLocalDateViaMilisecond(donorCert.getNotAfter()));
        alg.setValue(donorCert.getSigAlgName());

        final CryptoAlg cryptoAlg = CryptoAlgFactory.getInstance(donorCert.getSigAlgName());
        switch (cryptoAlg.getSignatureAlgorithm()) {
            case "GOST3411-2012-256WITHECGOST3410-2012-256":
                alg.setValue("gost2012_256");
                break;
            case "GOST3411-2012-512WITHECGOST3410-2012-512":
                alg.setValue("gost2012_512");
                break;
            case "GOST3411withECGOST3410":
                alg.setValue("gost2001");
                break;
            case "SHA256WithRSAEncryption":
                alg.setValue("rsa2048");
                break;
            default:
                throw new IllegalArgumentException("Ошибка в значении аргумента alg");
        }
        X500Name x500name = new JcaX509CertificateHolder(donorCert).getSubject();


        subjectFields.forEach(subjectField -> {
            try {
                final String value = x500name.getRDNs(subjectField.getAsn1ObjectIdentifier())[0].getFirst().getValue().toString();
                subjectField.setIsUsed(true);
                subjectField.setTextField(value);
                log.info("Поле Субъекта '{}'-'{}', прочитано и добавлено, значение '{}'", subjectField.getName(), subjectField.getAsn1ObjectIdentifier(), value);
            } catch (Throwable ignored) {
                subjectField.setIsUsed(false);
                log.info("Поле Субъекта '{}'-'{}', нету в исходном сертификате", subjectField.getName(), subjectField.getAsn1ObjectIdentifier());
            }
        });


        Map<String, ExtensionField> extensionFieldsMap = new HashMap<>();
        extensionFields.forEach(extensionField -> {
            extensionFieldsMap.put(extensionField.getOid(), extensionField);
        });
        for (String s : donorCert.getNonCriticalExtensionOIDs()) {
            log.info("Добавление расширения {}", s);
            try {
                if (!extensionFieldsMap.containsKey(s))
                    log.error("В сертификате было расширение, которого нет в программе {}", s);
                else
                    extensionFieldsMap.get(s).setFields(donorCert, caCert, false);
            } catch (Exception e) {
                log.error("В сертификате было расширение, которое неудалось обработать {}", s);
            }
        }
        for (String s : donorCert.getCriticalExtensionOIDs()) {
            log.info("Добавление расширения {}", s);
            try {
                if (!extensionFieldsMap.containsKey(s))
                    log.error("В сертификате было расширение, которого нет в программе {}", s);
                else
                    extensionFieldsMap.get(s).setFields(donorCert, caCert, true);
            } catch (Exception e) {
                log.error("В сертификате было расширение, которое неудалось обработать {}", s);
            }
        }

        //FIXME Я тут остановился
    }

    private LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    void generate(TextField serialTF, LocalDate dateFromTF, LocalDate dateToTF, String alg, Vector<SubjectField> subjectFields, Vector<ExtensionField> extensionFields, CheckBox selfSigned, TextField caCertificateTF, TextField caCertificatePkeyTF) throws Exception {
        if (alg == null) throw new IllegalArgumentException("Алгоритм шифрования не выбран");


        CryptoAlg cryptoAlg;
        switch (alg) {
            case "gost2012_256":
                cryptoAlg = CryptoAlgGost2012_256.getCryptoAlg();
                break;
            case "gost2012_512":
                cryptoAlg = CryptoAlgGost2012_512.getCryptoAlg();
                break;
            case "gost2001":
                cryptoAlg = CryptoAlgGost2001.getCryptoAlg();
                break;
            case "rsa2048":
                cryptoAlg = CryptoRSA.getCryptoAlg(2048);
                break;
            case "rsa4096":
                cryptoAlg = CryptoRSA.getCryptoAlg(4096);
                break;
            default:
                throw new IllegalArgumentException("Ошибка в значении аргумента alg");
        }


        CertAndKey certAndKey;
        Date dateFrom = Date.from(dateFromTF.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Date dateTo = Date.from(dateToTF.atStartOfDay(ZoneId.systemDefault()).toInstant());


        X500NameBuilder x500NameBld = new X500NameBuilder(CustomBCStyle.INSTANCE);
        for (int i = subjectFields.size() - 1; i >= 0; i--) {
            SubjectField value = subjectFields.elementAt(i);
            if (value.getIsUsed())
                x500NameBld.addRDN(CustomBCStyle.DefaultLookUp.get(value.getName().toLowerCase()), value.getTextField());
        }

        Vector<ExtensionObject> extensionParams = new Vector<>();
        for (ExtensionField extensionField : extensionFields) {
            if (extensionField.getIsUsedCheckBox())
                extensionParams.add(extensionField.buildExtensionObject());
        }

        CertificateCreator certificateCreator;
        if (selfSigned.isSelected()) {
            certificateCreator = new CertificateCreator(cryptoAlg);
            certAndKey = certificateCreator.generateCertificateV2(x500NameBld.build(), extensionParams, new BigInteger(serialTF.getText(), 16), dateFrom, dateTo);
        } else {
            X509Certificate caCert = null;
            PrivateKey caPrivateKey = null;
            try {
                File ca = new File(caCertificateTF.getText());
                File caPkey = new File(caCertificatePkeyTF.getText());
                byte[] bytes = FileManager.read(ca);
                caCert = CertEnveloper.decodeCert(bytes);
                caPrivateKey = CertEnveloper.decodePrivateKey(caPkey);
            } catch (IOException e) {
                throw new IOException("Ожидалось, что будет указан путь к сертификату УЦ, так как не отмечено 'Самоподписанный сертификат'" +
                        "\n" +
                        "или произошла какая-то другая ошибка чтения сертификата или приватного ключа УЦ (которым подписывается выпускаемый сертификат)", e);
            }
            certificateCreator = new CertificateCreator(cryptoAlg);
            certAndKey = certificateCreator.generateCertificateV2(x500NameBld.build(), extensionParams, new BigInteger(serialTF.getText(), 16), dateFrom, dateTo, caCert, caPrivateKey);
        }
        File saveFolder = new File("outputCerts");
        if (!saveFolder.exists())
            saveFolder.mkdirs();
        File output = new File("outputCerts/" + getThumbprint(certAndKey.getCertificate()));
        FileManager.write(new File(output.getAbsoluteFile() + ".cer"), CertEnveloper.encodeCert(certAndKey.getCertificate()));
        FileManager.write(new File(output.getAbsoluteFile() + ".pkey"), CertEnveloper.encodePrivateKey(certAndKey.getPrivateKey()));
        System.out.println("Записан сертификат " + output.getName());
    }
}
