package svkreml.krekerCa.core.creator.crl;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
public class RevokedCertificate {
    private BigInteger serialNumber;
    private Date date;
    private Integer reason;
}
