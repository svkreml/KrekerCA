package svkreml.krekerCa.core.cryptoAlg.impl;

import svkreml.krekerCa.core.cryptoAlg.CryptoAlg;

public abstract class CryptoAlgGost2012_512 {
    static public CryptoAlg getCryptoAlg() {
        return new CryptoAlg(
                "ECGOST3410-2012",
                "GOST3411-2012-512WITHECGOST3410-2012-512",
                "Tc26-Gost-3410-12-512-paramSetA",
                "BC",
                "X.509");
    }
}

/*
* Для 512-битной подписи:https://www.cryptopro.ru/forum2/default.aspx?g=posts&t=12090
id-tc26-gost-3410-12-512-paramSetA (szOID_tc26_gost_3410_12_512_paramSetA, 1.2.643.7.1.2.1.2.1, "ГОСТ Р 34.10-2012, 512 бит, параметры по умолчанию")
id-tc26-gost-3410-12-512-paramSetB (szOID_tc26_gost_3410_12_512_paramSetB, 1.2.643.7.1.2.1.2.2, "ГОСТ Р 34.10-2012, 512 бит, параметры ТК-26, набор B")
id-tc26-gost-3410-12-512-paramSetC (szOID_tc26_gost_3410_12_512_paramSetC, 1.2.643.7.1.2.1.2.3, "ГОСТ Р 34.10-2012, 512 бит, параметры ТК-26, набор С")
*
* */
