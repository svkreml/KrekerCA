package caJava.core.cryptoAlg;

import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_512;
import caJava.core.cryptoAlg.impl.CryptoRSA;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class CryptoAlgFactory {
    /*caCert.getSigAlgName()*/
    public static CryptoAlg getInstance(String alg) {
        System.out.println(alg);
        switch (alg) {
            case "1.2.643.2.2.3":
                return CryptoAlgGost2001.getCryptoAlg();
            case "1.2.643.7.1.1.3.2":
                return CryptoAlgGost2012_256.getCryptoAlg();
            case "1.2.643.7.1.1.3.3":
                return CryptoAlgGost2012_512.getCryptoAlg();
            case "SHA256withRSA":
                return CryptoRSA.getCryptoAlg(2048);
            case "???"://fixme rsa =2012_512
                return CryptoRSA.getCryptoAlg(2048);
            default:
                System.out.println(alg);
                return null;
        }

    }
}
