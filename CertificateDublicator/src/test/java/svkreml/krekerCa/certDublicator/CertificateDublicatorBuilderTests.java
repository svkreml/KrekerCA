package svkreml.krekerCa.certDublicator;

import org.bouncycastle.asn1.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import svkreml.krekerCa.core.BcInit;
import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.fileManagement.CertEnveloper;

import java.security.cert.X509Certificate;
import java.util.*;

public class CertificateDublicatorBuilderTests {
    private final static String certForCopy = "deleted";

    private static boolean traceRead = false;
    private static boolean traceCompare = false;

    private static void recurse(List<String> asn1AsList, ASN1Primitive obj) {

        if (obj instanceof DLSequence) {
            for (ASN1Encodable asn1Encodable : ((DLSequence) obj).toArray()) {
                recurse(asn1AsList, asn1Encodable.toASN1Primitive());
            }
        } else if (obj instanceof DLSet) {
            for (ASN1Encodable asn1Encodable : ((DLSet) obj).toArray()) {
                recurse(asn1AsList, asn1Encodable.toASN1Primitive());
            }
        } else if (obj instanceof DERTaggedObject) {
            recurse(asn1AsList, ((DERTaggedObject) obj).getObject().toASN1Primitive());
        }  else if (obj instanceof DLTaggedObject) {
            recurse(asn1AsList, ((DLTaggedObject) obj).getObject().toASN1Primitive());
        } else if (obj instanceof DERSequence) {
            for (ASN1Encodable asn1Encodable : ((DERSequence) obj).toArray()) {
                recurse(asn1AsList, asn1Encodable.toASN1Primitive());
            }
        } else {
            if (traceRead) {
                System.out.println(obj.getClass().getSimpleName() + "(" + obj.toString() + ")");
            }
            asn1AsList.add(obj.getClass().getSimpleName() + "(" + obj.toString() + ")");
        }
    }

    public static boolean compare(List<String> originalCertList, List<String> generatedCertList, List<Integer> ignoredNumbers) {
        boolean equals = true;
        if (originalCertList.size() != generatedCertList.size()) {
            System.out.println("originalCertList.size()=" + originalCertList.size() +
                    " != generatedCertList.size()=" + generatedCertList.size());
            equals = false;
        }
        for (int i = 0; i < originalCertList.size(); i++) {
            if (originalCertList.get(i).length() != (generatedCertList.get(i).length())) {
                System.out.println("-------------------------------------------------");
                System.out.println("    originalCertList.get(" + i + ")=" + originalCertList.get(i) +
                        "\n != generatedCertList.get(" + i + ")=" + generatedCertList.get(i));
                equals = false;
            } else
            if (!ignoredNumbers.contains(i)) {
                if (!originalCertList.get(i).equals(generatedCertList.get(i))) {
                    System.out.println("-------------------------------------------------");
                    System.out.println("    originalCertList.get(" + i + ")=" + originalCertList.get(i) +
                            "\n != generatedCertList.get(" + i + ")=" + generatedCertList.get(i));
                    equals = false;
                }
            }
        }
        return equals;
    }

    @Test
    public void certSelfSignedCopyTest() throws Exception {
        BcInit.init();
        X509Certificate donorX509Certificate = CertEnveloper.decodeCert(Base64.getMimeDecoder().decode(certForCopy));
        CertificateDublicatorBuilder.CertificateDublicatorBuilderBuilder builder = CertificateDublicatorBuilder.builder();
        builder.donorCertificate(donorX509Certificate)
                .notBefore(Objects.requireNonNull(donorX509Certificate).getNotBefore())
                .notAfter(donorX509Certificate.getNotAfter());
        final CertAndKey certAndKey = builder.build().copyCertificate();


        List<String> originalCertList = new ArrayList<>();
        recurse(originalCertList, new ASN1InputStream(Base64.getMimeDecoder().decode(certForCopy)).readObject());
        List<String> generatedCertList = new ArrayList<>();
        recurse(generatedCertList, new ASN1InputStream(certAndKey.getCertificate().getEncoded()).readObject());

        /*с данными номерами сравниваться поля не будут, сравниваться будет только длина полей*/
        List<Integer> ignoredNumbers = new ArrayList<>(Arrays.asList(44, 58, 60));

        System.out.println("-----BEGIN CERTIFICATE-----");
        System.out.println(new String(Base64.getMimeEncoder().encode(certAndKey.getCertificate().getEncoded())));
        System.out.println("-----END CERTIFICATE-----");
        Assert.assertTrue(compare(originalCertList, generatedCertList, ignoredNumbers),"Первый и второй сертификат не равны по полям (подпись, открытый ключ и 2.5.29.14 игнориуется)");
    }

}
