package caJava.core.cryptoAlg.impl;

import caJava.core.cryptoAlg.CryptoAlg;

public abstract class CryptoAlgGost2012_256  {
    static public CryptoAlg getCryptoAlg() {
        return new CryptoAlg(
                "ECGOST3410-2012",
                "GOST3411-2012-256WITHECGOST3410-2012-256",
                "GostR3410-2001-CryptoPro-XchA",
                "BC",
                "X.509");
    }
}
// this.ellipticCurve =  "Tc26-Gost-3410-12-256-paramSetA";