package svkreml.krekerCa.ocspServer.config;

import lombok.Data;

import java.util.List;

@Data
public class OcspConfig {
    Integer serverPort;
    List<ServletInstance> servletInstances;
}
