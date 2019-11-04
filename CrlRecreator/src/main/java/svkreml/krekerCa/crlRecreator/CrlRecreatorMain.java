package svkreml.krekerCa.crlRecreator;

import lombok.extern.slf4j.Slf4j;
import svkreml.krekerCa.core.BcInit;
import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.core.creator.crl.CrlCreator;
import svkreml.krekerCa.core.creator.crl.RevokedCertificate;
import svkreml.krekerCa.crlRecreator.dto.Config;
import svkreml.krekerCa.crlRecreator.dto.CrlConfig;
import svkreml.krekerCa.crlRecreator.dto.RevokedDto;
import svkreml.krekerCa.fileManagement.FileManager;
import svkreml.krekerCa.fileManagement.Json;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Slf4j
public class CrlRecreatorMain {
    private static Config config;

    public static void main(String[] args) {
        try {
            log.info("****************************************************************");
            log.info("***----------------------------------------------------------***");
            log.info("****************************************************************");
            log.info("Program crlRecreator started");
            log.info("**********************************");
            BcInit.init();
            log.info("BC crypto provider loaded");
            File file = null;
            try {
                file = new File("CrlRecreatorConfig.json");
                config = (Config) Json.readValue(Config.class, file);
            } catch (Exception e) {
                throw new IOException("Problems with reading config: " + Objects.requireNonNull(file).getAbsolutePath());
            }
            log.info("Config readed");
            while (true) {
                log.info("**********************************");
                log.info("********Starting iteration********");
                log.info("**********************************");
                for (CrlConfig crlConfig : config.getCrlConfigList()) {
                    generateCrl(crlConfig);
                }
                log.info("Sleep for " + config.getRecreateTimerSeconds() + " seconds");
                Thread.sleep(config.getRecreateTimerSeconds() * 1000);
            }
        } catch (Exception e) {
            log.error("Critical error", e);
            System.out.println("Critical error");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void generateCrl(CrlConfig crlConfig) throws IOException, NoSuchAlgorithmException, CRLException, ParseException {
        log.info("Reading cert & pkey \n\t{}\n\t{}",
                crlConfig.getCertificatePath(),
                crlConfig.getPrivateKeyPath()
        );


        ArrayList<RevokedCertificate> revokedCertificates = new ArrayList<>(crlConfig.getRevokedDtoLisconfig().size());
        for (RevokedDto r : crlConfig.getRevokedDtoLisconfig()) {
            revokedCertificates.add(new RevokedCertificate(
                    r.toSerial(),
                    r.toDate(),
                    r.getCrlReason()
            ));
        }

        CrlCreator.CrlCreatorBuilder builder = CrlCreator.builder();
        builder.generateDate(new Date(new Date().getTime() + config.getCrlGenerateTimeoffset() * 1000))
                .nextUpdate(new Date(new Date().getTime() + config.getCrlTTLseconds() * 1000)).
                serialNumber(BigInteger.valueOf(new Date().getTime()))
                .singingCertAndKey(CertAndKey.getPKCS8FromDisk(crlConfig.getCertificatePath(), crlConfig.getPrivateKeyPath()))
                .revokedCertificates(revokedCertificates);

        File output = new File(crlConfig.getCrlPath());
        FileManager.write(output.getAbsoluteFile(), builder.build().generate().getEncoded());
        log.info("Saved " + output.getAbsoluteFile().getAbsolutePath());
    }
}
