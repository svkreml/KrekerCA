package caJava.core.cryptoAlg.impl;

import caJava.core.cryptoAlg.CryptoAlg;

public abstract class CryptoAlgGost2001 {
   static public CryptoAlg getCryptoAlg() {
        return new CryptoAlg(
        "ECGOST3410",
        "GOST3411withECGOST3410",
        "GostR3410-2001-CryptoPro-XchA",
        "BC",
        "X.509");
    }
}
