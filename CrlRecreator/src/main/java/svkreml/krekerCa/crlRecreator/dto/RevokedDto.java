package svkreml.krekerCa.crlRecreator.dto;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RevokedDto {

    private String serialHex;
    private Integer crlReason;
    private String addDate;

    public String getSerialHex() {
        return serialHex;
    }

    public void setSerialHex(String serialHex) {
        this.serialHex = serialHex;
    }

    public Integer getCrlReason() {
        return crlReason;
    }

    public void setCrlReason(Integer crlReason) {
        this.crlReason = crlReason;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public BigInteger toSerial() throws ParseException {
        return new BigInteger(serialHex, 16);
    }
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public Date toDate() throws ParseException {
        return dateFormatter.parse(addDate);
    }

}
