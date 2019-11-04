package svkreml.krekerCa.core.cryptoAlg.impl;

import svkreml.krekerCa.core.cryptoAlg.CryptoAlg;

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
