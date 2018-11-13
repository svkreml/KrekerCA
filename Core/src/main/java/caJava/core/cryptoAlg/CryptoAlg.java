package caJava.core.cryptoAlg;

import java.util.logging.Logger;

public class CryptoAlg {
    private static Logger logger = Logger.getLogger(CryptoAlg.class.getName());

    public String getAlgorithm() {
        return algorithm;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String algorithm;
    public String signatureAlgorithm;
    public String ellipticCurve;
    public String cryptoProvider;
    public String certificateType;
    public Integer keyLength;


    public CryptoAlg(String algorithm, String signatureAlgorithm, Integer keyLength, String cryptoProvider, String certificateType) {
        this.algorithm = algorithm;
        this.signatureAlgorithm = signatureAlgorithm;
        this.keyLength = keyLength;
        this.cryptoProvider = cryptoProvider;
        this.certificateType = certificateType;
        logger.info("Алгоритм генерации: " + this.toString());
    }
    public CryptoAlg(String algorithm, String signatureAlgorithm, String ellipticCurve, String cryptoProvider, String certificateType) {
        this.algorithm = algorithm;
        this.signatureAlgorithm = signatureAlgorithm;
        this.ellipticCurve = ellipticCurve;
        this.cryptoProvider = cryptoProvider;
        this.certificateType = certificateType;
        logger.info("Алгоритм генерации: " + this.toString());
    }

    @Override
    public String toString() {
        return "CryptoAlg{" +
                "algorithm='" + algorithm + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                ", ellipticCurve='" + ellipticCurve + '\'' +
                ", cryptoProvider='" + cryptoProvider + '\'' +
                ", certificateType='" + certificateType + '\'' +
                '}';
    }
}
