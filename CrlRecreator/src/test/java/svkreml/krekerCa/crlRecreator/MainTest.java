package svkreml.krekerCa.crlRecreator;


import svkreml.krekerCa.fileManagement.Json;
import svkreml.krekerCa.crlRecreator.dto.Config;
import svkreml.krekerCa.crlRecreator.dto.CrlConfig;
import svkreml.krekerCa.crlRecreator.dto.RevokedDto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MainTest {


    public static void main(String[] args) throws IOException {
        Config config = new Config();

        config.setCrlGenerateTimeoffset(-1000);

        config.setCrlTTLseconds(3600*5);

        config.setRecreateTimerSeconds(10);

        config.setCrlConfigList(new ArrayList<CrlConfig>());

        final CrlConfig crlConfig = new CrlConfig();

        crlConfig.setCertificatePath("certForCA\\output\\etalon\\ca2000000000002.cer");
        crlConfig.setPrivateKeyPath("certForCA\\output\\etalon\\ca2000000000002.cer.pkey");
        crlConfig.setCrlPath("certForCA\\output\\ca2000000000002.crl");

        crlConfig.setRevokedDtoLisconfig(new ArrayList<RevokedDto>());


        final RevokedDto revokedDto = new RevokedDto();

        revokedDto.setAddDate("11/12/2015 12:34:56");
        revokedDto.setSerialHex("1234567890");
        revokedDto.setCrlReason(0);

        crlConfig.getRevokedDtoLisconfig().add(revokedDto);

        config.getCrlConfigList().add(crlConfig);

        Json.write(config, new File("config.json"));

        final Config o = (Config) Json.readValue(Config.class, new File("config.json"));
        System.out.println(o);
    }
}
/*
 * CRLReason ::= ENUMERATED {
 *  unspecified             (0),
 *  keyCompromise           (1),
 *  cACompromise            (2),
 *  affiliationChanged      (3),
 *  superseded              (4),
 *  cessationOfOperation    (5),
 *  certificateHold         (6),
 *  removeFromCRL           (8),
 *  privilegeWithdrawn      (9),
 *  aACompromise           (10)
 * }*/
