package svkreml.krekerCa.ocspServer;

import svkreml.krekerCa.fileManagement.Json;
import svkreml.krekerCa.ocspServer.config.OcspConfig;
import svkreml.krekerCa.ocspServer.config.ServletInstance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigGenerator {
    public static void main(String[] args) throws IOException {
        OcspConfig ocspConfig = new OcspConfig();
        ocspConfig.setServerPort(8082);


        ServletInstance servletInstance = new ServletInstance();
        servletInstance.setServerPath("/ocsp");
        servletInstance.setCaCertPath("setCaCertPath");
        servletInstance.setOcspSingingCertPath("setOcspSingingCertPath");
        servletInstance.setOcspSingingPkeyPath("setOcspSingingPkeyPath");
        servletInstance.setCrlPath("setOcspSingingPkeyPath");


        List<ServletInstance> l = new ArrayList<>();
        l.add(servletInstance);
        l.add(servletInstance);
        ocspConfig.setServletInstances(l);

        Json.write(ocspConfig, new File("ocspConfigTest.json"));
    }
}
