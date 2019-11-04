package svkreml.krekerCa.ocspServer.config;


import lombok.Data;

@Data
public class ServletInstance {
    String serverPath;
    String caCertPath;
    String ocspSingingCertPath;
    String ocspSingingPkeyPath;
    String crlPath;
}
