import caJava.core.CertAndKey;
import caJava.core.creator.CertificateCreator;
import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.core.wrapper.ExtensionsMap;
import caJava.core.wrapper.SubjectMap;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;

public class Program {
    static public void createCert(LinkedHashMap<String, String> params) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException, CertificateException, OperatorCreationException {


        String alg = params.getOrDefault("alg","gost2012_256");
        File subject = new File(params.get("subject"));
        File extensions = new File(params.get("extensions"));


        //Если указан контейнер с СА, то ...
        File ca = null;
        String caPass = null;
        boolean isCa = true;
        if (params.get("ca") != null) {
            ca = new File(params.get("caFile"));
            caPass = params.get("caPass");
        }

        //fixme добавить параметров
        CryptoAlg cryptoAlg;
        switch (alg) {
            case "gost2012_256":
                cryptoAlg = CryptoAlgGost2012_256.getCryptoAlg();
                break;
                default:
                    throw new IllegalArgumentException("Ошибка в значении аргумента alg");
        }
        CertificateCreator certificateCreator = new CertificateCreator(cryptoAlg);

        //fixMe брать дату из параметров
        Date startDate = Date.from(Date.from(LocalDate.parse(params.get("dateFrom"), DateTimeFormatter.ISO_DATE).atStartOfDay(ZoneOffset.UTC).toInstant()).toInstant());
        Date endDate = Date.from(Date.from(LocalDate.parse(params.get("dateTo"), DateTimeFormatter.ISO_DATE).atStartOfDay(ZoneOffset.UTC).toInstant()).toInstant());

        CertAndKey certAndKey = certificateCreator.generateCertificate(SubjectMap.get(subject), ExtensionsMap.getVector(extensions), BigInteger.valueOf(123), startDate, endDate);


        //fixme сохранять pfx и имя файла из параметра
        if (params.get("outputDer") != null) {
            File output = new File(params.get("outputDer"));
            FileManager.write(output, CertEnveloper.encodeCert(certAndKey.getCertificate()));
            FileManager.write(new File(output.getAbsoluteFile() + ".pkey"), CertEnveloper.encodePrivateKey(certAndKey.getPrivateKey()));
        }
        if (params.get("outputPfx") != null) {
            File output = new File(params.get("outputPfx"));
            String pass = params.get("pass");
            System.out.println("save as pfx"); //todo "save as pfx"
        }


    }
}
