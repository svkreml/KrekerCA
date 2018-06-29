package caJava.сore;

import caJava.core.CertAndKey;
import caJava.core.creator.CertificateCreator;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.core.extensions.ExtensionParam;
import caJava.core.pfx.PfxUtils;
import caJava.customOID.CustomBCStyle;
import org.bouncycastle.asn1.DERNumericString;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.operator.OperatorCreationException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Vector;

public class PfxUtilsTests {
    @Test
    public void savePfxToFile() throws OperatorCreationException, CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, NoSuchProviderException, InvalidAlgorithmParameterException {
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
        x500NameBld.addRDN(BCStyle.O, "Мимими11111111111111111");
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
        CertAndKey certAndKeyCa = certificateCreator.generateCertificate(x500NameBld.build(), extensions,serial, startDate, endDate, null, null);
        PfxUtils.convertToPfx(certAndKeyCa, "keyTestCa", "1234567890", new File("test.pfx"));

      //  FileManager.write(new File("cer_t.der"), CertEnveloper.encodeCert(certAndKeyCa.getCertificate()));
      //  FileManager.write(new File("cer_t.pkey"), CertEnveloper.encodePrivateKey(certAndKeyCa.getPrivateKey()));
    }
}
