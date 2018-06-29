package caJava.core.cryptoAlg.impl;

import caJava.core.cryptoAlg.CryptoAlg;

public class CryptoRSA {
    static public CryptoAlg getCryptoAlg(int keyLength) {
        return new CryptoAlg(
                "RSA",
                "SHA256WithRSAEncryption",
                keyLength,// keyPairGenerator.initialize(1024, new SecureRandom());
                "BC",
                "X.509");
    }
}
