package caJava.customOID;
/*
*Нужно убрать в конфиг файлы, а этот убрать вообще
*
Средства электронной подписи и УЦ издателя
issuerSignTool
	1.2.643.100.112

Средства электронной подписи владельца
subjectSignTool
	1.2.643.100.111
* */
public class CustomText {



/*        1.2.643.100.112
        ASN1EncodableVector v = new ASN1EncodableVector();

        certificate.addExtension(CustomExtension.issuerSignTool, false, new BERSequence(v));*/

    static public String issuerSignTool_1="\"Крекер CSP\" (версия 0.0 beta)";
    static public String issuerSignTool_2="\"Крекер УЦ\" версия 0.0 beta";
    static public String issuerSignTool_3="Сертификат соответствия № СФ/999-9999 от 00.01.2025";
    static public String issuerSignTool_4="Сертификат соответствия № СФ/999-9999 от 00.04.2025";
}
