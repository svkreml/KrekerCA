package caJava.сore;

import caJava.core.CertAndKey;
import caJava.core.creator.CertificateCreator;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.core.extensions.ExtensionParam;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.customOID.CustomBCStyle;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.asn1.DERNumericString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.operator.OperatorCreationException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Vector;

public class CertificateCreatorTests {

    @Test
    public void createUserCertNew() throws IOException, CertificateException, NoSuchAlgorithmException, OperatorCreationException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Date startDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date endDate = Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
//

        CertificateCreator certificateCreator = new CertificateCreator(CryptoAlgGost2012_256.getCryptoAlg());

        X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);
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
        x500NameBld.addRDN(BCStyle.CN, "НИИ \"user Крекер\"");


        X509Certificate caCert = CertEnveloper.decodeCert(FileManager.read(new File("testgost.der")));
        PrivateKey caCertPrivateKey = CertEnveloper.decodePrivateKey(new File("testgost.pkey"));

        Vector<ExtensionParam> extensions = new Vector<>();
        extensions.add(new ExtensionParam("keyUsage", "false", "86"));
        extensions.add(new ExtensionParam("subjectKeyIdentifier", "false"));
        //extensions.add(new ExtensionParam("1.3.6.1.4.1.311.20.2", "false", "subCA"));
        extensions.add(new ExtensionParam("certificatePolicies", "false", "1.2.643.100.113.1"));
        extensions.add(new ExtensionParam("subjectSignTool", "false", "123123123123"));
        extensions.add(new ExtensionParam("1.3.6.1.4.1.311.21.1", "false", "0"));
        extensions.add(new ExtensionParam("authorityKeyIdentifier", "false"));
        extensions.add(new ExtensionParam("cRLDistributionPoints", "false", "http://localhost/revoked.crl"));
        extensions.add(new ExtensionParam("issuerSignTool", "false", "123123123123", "123123123123", "123123123123", "123123123123123123"));
        extensions.add(new ExtensionParam("basicConstraints", "true", "0"));

        //Генерация серийного номера
        SecureRandom random=  new SecureRandom();
        byte[] id = new byte[20];
        random.nextBytes(id);
        BigInteger serial = new BigInteger(160, random);
        //создание корневого сертификата
        CertAndKey certAndKeyCa = certificateCreator.generateCertificate(x500NameBld.build(), extensions, serial, startDate, endDate, caCert, caCertPrivateKey);
        FileManager.write(new File("outputCer/user.der"), CertEnveloper.encodeCert(certAndKeyCa.getCertificate()));
        FileManager.write(new File("outputCer/user.pkey"), CertEnveloper.encodePrivateKey(certAndKeyCa.getPrivateKey()));
    }

    @Test
    public void createSubCaCertNew() throws IOException, CertificateException, NoSuchAlgorithmException, OperatorCreationException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Date startDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date endDate = Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
//

        CertificateCreator certificateCreator = new CertificateCreator(CryptoAlgGost2001.getCryptoAlg());

        X500NameBuilder x500NameBld = new X500NameBuilder(CustomBCStyle.INSTANCE);
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
        x500NameBld.addRDN(BCStyle.CN, "НИИ \"Суб Крекер\"");


        X509Certificate caCert = CertEnveloper.decodeCert(FileManager.read(new File("etalon.der")));
        PrivateKey caCertPrivateKey = CertEnveloper.decodePrivateKey(new File("etalon.pkey"));

        Vector<ExtensionParam> extensions = new Vector<>();
        extensions.add(new ExtensionParam("keyUsage", "false", "86"));
        extensions.add(new ExtensionParam("subjectKeyIdentifier", "false"));
        extensions.add(new ExtensionParam("1.3.6.1.4.1.311.20.2", "false", "subCA"));
        extensions.add(new ExtensionParam("certificatePolicies", "false", "1.2.643.100.113.1"));
        extensions.add(new ExtensionParam("subjectSignTool", "false", "123123213123212"));
        extensions.add(new ExtensionParam("1.3.6.1.4.1.311.21.1", "false", "0"));
        extensions.add(new ExtensionParam("authorityKeyIdentifier", "false"));
        extensions.add(new ExtensionParam("cRLDistributionPoints", "false", "http://localhost/revoked.crl"));
        extensions.add(new ExtensionParam("issuerSignTool", "false", "123123123", "123123123", "123123123", "123123123123"));
        extensions.add(new ExtensionParam("basicConstraints", "true", "0"));

        //Генерация серийного номера
        SecureRandom random=  new SecureRandom();
        byte[] id = new byte[20];
        random.nextBytes(id);
        BigInteger serial = new BigInteger(160, random);
        //создание корневого сертификата
        CertAndKey certAndKeyCa = certificateCreator.generateCertificate(x500NameBld.build(), extensions,serial, startDate, endDate, caCert, caCertPrivateKey);
        FileManager.write(new File("sub_cer_t.der"), CertEnveloper.encodeCert(certAndKeyCa.getCertificate()));
        FileManager.write(new File("sub_cer_t.pkey"), CertEnveloper.encodePrivateKey(certAndKeyCa.getPrivateKey()));
    }

    @Test
    public void createCaCertNew() throws IOException, CertificateException, NoSuchAlgorithmException, OperatorCreationException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Date startDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date endDate = Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
//

        CertificateCreator certificateCreator = new CertificateCreator(CryptoAlgGost2012_256.getCryptoAlg());
        Vector<ExtensionParam> extensions = new Vector<>();
        //Имя
        X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);

/*"..".replaceAll("-----.*-----","").getBytes();
        Base64.getEncoder().encodeToString()*/
        x500NameBld.addRDN(CustomBCStyle.СНИЛС, new DERNumericString("12345678901"));
        x500NameBld.addRDN(BCStyle.EmailAddress, "adfsadf@asdfsdf.ru");
        x500NameBld.addRDN(BCStyle.C, "RU");
        x500NameBld.addRDN(BCStyle.ST, "77 Москва");
        x500NameBld.addRDN(BCStyle.L, ("г. Москва"));
        x500NameBld.addRDN(BCStyle.STREET, "улица");
        x500NameBld.addRDN(BCStyle.O, "Мимими");
        x500NameBld.addRDN(CustomBCStyle.ОГРН, new DERNumericString("1212121212121"));
        x500NameBld.addRDN(CustomBCStyle.ИНН, new DERNumericString("0012345678"));
        x500NameBld.addRDN(BCStyle.CN, "гуц");

        // Расширения
        extensions.add(new ExtensionParam("issuerSignTool", "false", "123123123", "123123123", "123123123", "123123123"));
        extensions.add(new ExtensionParam("subjectSignTool", "false", "123123123123"));
        extensions.add(new ExtensionParam("certificatePolicies", "false", "1.2.643.100.113.1", "1.2.643.100.113.2", "1.2.643.100.113.3", "1.2.643.100.113.4", "1.2.643.100.113.5", "2.5.29.32.0"));
        extensions.add(new ExtensionParam("subjectKeyIdentifier", "false"));
        extensions.add(new ExtensionParam("keyUsage", "true", "6"));
        extensions.add(new ExtensionParam("basicConstraints", "true", "true"));


        //Генерация серийного номера
        SecureRandom random=  new SecureRandom();
        byte[] id = new byte[20];
        random.nextBytes(id);
        BigInteger serial = new BigInteger(160, random);



        //создание корневого сертификата
        CertAndKey certAndKeyCa = certificateCreator.generateCertificate(x500NameBld.build(), extensions, serial, startDate, endDate, null, null);


        FileManager.write(new File("cer_t.der"), CertEnveloper.encodeCert(certAndKeyCa.getCertificate()));
        FileManager.write(new File("cer_t.pkey"), CertEnveloper.encodePrivateKey(certAndKeyCa.getPrivateKey()));
    }

    @Test
    public void createCaCert() throws IOException, CertificateException, NoSuchAlgorithmException, OperatorCreationException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Date startDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date endDate = Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());


        CertificateCreator certificateCreator = new CertificateCreator(CryptoAlgGost2001.getCryptoAlg());

        LinkedHashMap<String, Object> nameCa = new LinkedHashMap<>();
        //nameCa.put("СНИЛС", new DERNumericString("12312312312"));
        nameCa.put("ОГРН", new DERNumericString("1231231231231"));
        nameCa.put("ИНН", new DERNumericString("0012345678"));
        nameCa.put("STREET", "улица Улица, дом 84");
        nameCa.put("EmailAddress", "mail@test.ru");
        nameCa.put("C", "RU");
        nameCa.put("ST", "77 Москва");
        nameCa.put("L", "г. Москва");
        nameCa.put("O", "НИИ «Крекер»");
        nameCa.put("T", "Начальник отдела крекеров");
        nameCa.put("GIVENNAME", "Иван Иванович");
        nameCa.put("SURNAME", "Иванов");
        nameCa.put("CN", "НИИ «Крекер»");


        //создание корневого сертификата
        CertAndKey certAndKeyCa = certificateCreator.createCertCa(nameCa, startDate, endDate);
        FileManager.write(new File("cer_t.der"), CertEnveloper.encodeCert(certAndKeyCa.getCertificate()));
        FileManager.write(new File("etalon.pkey"), CertEnveloper.encodePrivateKey(certAndKeyCa.getPrivateKey()));
    }

    @Test
    public void createCertFromCa() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CertificateException, OperatorCreationException, IOException {
        Date startDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date endDate = Date.from(LocalDate.of(2035, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());

        CertificateCreator certificateCreator = new CertificateCreator(CryptoAlgGost2001.getCryptoAlg());
        //заугрузка корневого сертификата из файлов
        X509Certificate caCert = CertEnveloper.decodeCert(FileManager.read(new File("cer.der")));
        PrivateKey caCertPrivateKey = CertEnveloper.decodePrivateKey(new File("cer.pkey"));

        LinkedHashMap<String, Object> name = new LinkedHashMap<>();
        name.put("СНИЛС", new DERNumericString("0012345678"));
        name.put("ОГРН", new DERNumericString("0012345678"));
        name.put("ИНН", new DERNumericString("0012345678"));
        name.put("STREET", new DERUTF8String("улица Улица, дом 84"));
        name.put("EmailAddress", new DERUTF8String("mail@test.ru"));
        name.put("C", new DERUTF8String("RU"));
        name.put("ST", new DERUTF8String("77 Москва"));
        name.put("L", new DERUTF8String("г. Москва"));
        name.put("O", new DERUTF8String("НИИ \"Крекер\""));
        name.put("T", new DERUTF8String("Начальник отдела крекеров"));
        name.put("GIVENNAME", new DERUTF8String("Иван Иванович"));
        name.put("SURNAME", new DERUTF8String("Иванов"));
        name.put("CN", new DERUTF8String("Дыня"));

        CertAndKey certAndKey = certificateCreator.createCert(name, startDate, endDate, caCert, caCertPrivateKey);

        FileManager.write(new File("cer_t_sub.der"), CertEnveloper.encodeCert(certAndKey.getCertificate()));
        FileManager.write(new File("cer_t_sub.pkey"), CertEnveloper.encodePrivateKey(certAndKey.getPrivateKey()));

    }
}
