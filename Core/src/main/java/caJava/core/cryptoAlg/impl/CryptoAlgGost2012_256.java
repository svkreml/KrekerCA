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
/*
// this.ellipticCurve =  "Tc26-Gost-3410-12-256-paramSetA";
Для 256-битной подписи:https://www.cryptopro.ru/forum2/default.aspx?g=posts&t=12090
id-GostR3410-2001-CryptoPro-A-ParamSet (szOID_GostR3410_2001_CryptoPro_A_ParamSet, 1.2.643.2.2.35.1, "ГОСТ Р 34.10-2001, параметры по умолчанию")
id-GostR3410-2001-CryptoPro-XchA-ParamSet (szOID_GostR3410_2001_CryptoPro_XchA_ParamSet, 1.2.643.2.2.36.0, "ГОСТ Р 34.10-2001, параметры обмена по умолчанию")
id-GostR3410-2001-CryptoPro-B-ParamSet (szOID_GostR3410_2001_CryptoPro_B_ParamSet, 1.2.643.2.2.35.2, "ГОСТ Р 34.10-2001, параметры Оскар 2.x")
id-GostR3410-2001-CryptoPro-XchB-ParamSet (szOID_GostR3410_2001_CryptoPro_XchB_ParamSet, 1.2.643.2.2.36.1, "ГОСТ Р 34.10-2001, параметры обмена 1")
id-GostR3410-2001-CryptoPro-C-ParamSet (szOID_GostR3410_2001_CryptoPro_C_ParamSet, 1.2.643.2.2.35.3, "ГОСТ Р 34.10-2001, параметры подписи 1")
id-tc26-gost-3410-2012-256-paramSetA (szOID_tc26_gost_3410_12_256_paramSetA, 1.2.643.7.1.2.1.1.1, "ГОСТ Р 34.10-2012, 256 бит, параметры ТК-26, набор A")
*/


