package svkreml.krekerCa.crlRecreator.dto;

import java.util.List;

public class Config {
    private Integer recreateTimerSeconds;
    private Integer crlGenerateTimeoffset;
    private Integer crlTTLseconds;
    private List<CrlConfig> crlConfigList;

    public Integer getCrlGenerateTimeoffset() {
        return crlGenerateTimeoffset;
    }

    public void setCrlGenerateTimeoffset(Integer crlGenerateTimeoffset) {
        this.crlGenerateTimeoffset = crlGenerateTimeoffset;
    }

    public Integer getCrlTTLseconds() {
        return crlTTLseconds;
    }

    public void setCrlTTLseconds(Integer crlTTLseconds) {
        this.crlTTLseconds = crlTTLseconds;
    }

    public Integer getRecreateTimerSeconds() {
        return recreateTimerSeconds;
    }

    public void setRecreateTimerSeconds(Integer recreateTimerSeconds) {
        this.recreateTimerSeconds = recreateTimerSeconds;
    }

    public List<CrlConfig> getCrlConfigList() {
        return crlConfigList;
    }

    public void setCrlConfigList(List<CrlConfig> crlConfigList) {
        this.crlConfigList = crlConfigList;
    }
}
