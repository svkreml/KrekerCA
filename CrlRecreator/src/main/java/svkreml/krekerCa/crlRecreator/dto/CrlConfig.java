package svkreml.krekerCa.crlRecreator.dto;

import java.util.List;

public class CrlConfig {
    private String certificatePath;
    private String privateKeyPath;
    private String crlPath;
    private List<RevokedDto> revokedDtoLisconfig;

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getCrlPath() {
        return crlPath;
    }

    public void setCrlPath(String crlPath) {
        this.crlPath = crlPath;
    }

    public List<RevokedDto> getRevokedDtoLisconfig() {
        return revokedDtoLisconfig;
    }

    public void setRevokedDtoLisconfig(List<RevokedDto> revokedDtoLisconfig) {
        this.revokedDtoLisconfig = revokedDtoLisconfig;
    }
}
