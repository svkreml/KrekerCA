package caJava.core.cryptoAlg;

import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.core.cryptoAlg.impl.CryptoRSA;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class CryptoAlgFactory {
    public static CryptoAlg getInstance(String alg) {
        switch (alg) {
            case "ECGOST3410":
                return CryptoAlgGost2001.getCryptoAlg();
            case "ECGOST3410-2012":
                return CryptoAlgGost2012_256.getCryptoAlg();
            case "RSA":
                return CryptoRSA.getCryptoAlg(2048);
            default:
                return null;
        }

    }
}
