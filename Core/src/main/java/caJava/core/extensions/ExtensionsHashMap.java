package caJava.core.extensions;

import caJava.customOID.CustomExtension;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.function.BiFunction;

public class ExtensionsHashMap extends HashMap<String, BiFunction<CertBuildContainer, String[], Boolean>> {
    public ExtensionsHashMap() {

        /*2.5.29.16 Идентификатор ключа субъекта -- Открытый ключ субъекта*/
        put("privateKeyUsagePeriod", ((certBuildContainer, params) -> {
            try {
                Date from;
                Date to;
                if (params == null || params.length < 1)
                    params = new String[]{"false"};
                if(params.length<3){
                    from = certBuildContainer.getFrom();
                    to =certBuildContainer.getTo();
                }else {
                    from = new Date(Long.valueOf(params[1]));
                    to = new Date(Long.valueOf(params[2]));
                }
                ASN1EncodableVector v = new ASN1EncodableVector();
                DERGeneralizedTime fromTime = new DERGeneralizedTime(from);
                v.add(new DERTaggedObject(false, 0, fromTime));

                DERGeneralizedTime toTime = new DERGeneralizedTime(to);
                v.add(new DERTaggedObject(false, 1, toTime));

                PrivateKeyUsagePeriod pkup = PrivateKeyUsagePeriod.getInstance(new DERSequence(v));

                certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.privateKeyUsagePeriod,
                        Boolean.valueOf(params[0]), pkup);
                return true;

            } catch (CertIOException e) {
                e.printStackTrace();
            }
            return false;
        }));

        /*2.5.29.14 Идентификатор ключа субъекта -- Открытый ключ субъекта*/
        put("subjectKeyIdentifier", ((certBuildContainer, params) -> {
            try {
                if (params == null || params.length < 1)
                    params = new String[]{"false"};
                certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.subjectKeyIdentifier,
                        Boolean.valueOf(params[0]), new JcaX509ExtensionUtils().createSubjectKeyIdentifier((certBuildContainer.getKeyPair()).getPublic()));
                return true;

            } catch (NoSuchAlgorithmException | CertIOException e) {
                e.printStackTrace();
            }
            return false;
        }));

        /*Основные ограничения	2.5.29.19*/
        put("basicConstraints", ((certBuildContainer, params) -> {
            try {
                if (params == null || params.length < 2)
                    params = new String[]{"true", "true"};
                BasicConstraints constraints;
                if (params[1].contains("e"))
                    constraints = new BasicConstraints(Boolean.valueOf(params[1]));
                else
                    constraints = new BasicConstraints(Integer.parseInt(params[1]));
                certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.basicConstraints, Boolean.valueOf(((String[]) params)[0]), constraints.getEncoded());
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));

        /*1.2.643.100.112*/
        put("issuerSignTool", ((certBuildContainer, params) -> {
            try {
                ASN1EncodableVector lines = new ASN1EncodableVector();
                for (int i = 1; i < params.length; i++) {
                    lines.add(new DERUTF8String(params[i]));
                }
                certBuildContainer.getX509v3CertificateBuilder().addExtension(CustomExtension.issuerSignTool, Boolean.valueOf(params[0]), new BERSequence(lines));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));
        //Имя шаблона сертификата
        put("1.3.6.1.4.1.311.20.2", ((certBuildContainer, params) -> {
            try {
                if (params.length == 2) {
                    certBuildContainer.getX509v3CertificateBuilder().addExtension(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.20.2").intern(), Boolean.valueOf(params[0]), new DERUTF8String(params[1]));
                    return true;
                } else if (params.length > 2) {
                    ASN1EncodableVector lines = new ASN1EncodableVector();
                    for (int i = 1; i < params.length; i++) {
                        lines.add(new DERUTF8String(params[i]));
                    }
                    certBuildContainer.getX509v3CertificateBuilder().addExtension(CustomExtension.subjectSignTool, Boolean.valueOf(params[0]), new BERSequence(lines));
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));
        //Версия ЦС
        put("1.3.6.1.4.1.311.21.1", ((certBuildContainer, params) -> {
            try {
                if (params.length == 2) {
                    certBuildContainer.getX509v3CertificateBuilder().addExtension(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.21.1").intern(), Boolean.valueOf(params[0]), new ASN1Integer(Integer.parseInt(params[1])));
                    return true;
                } else if (params.length > 2) {
                    ASN1EncodableVector lines = new ASN1EncodableVector();
                    for (int i = 1; i < params.length; i++) {
                        lines.add(new DERUTF8String(params[i]));
                    }
                    certBuildContainer.getX509v3CertificateBuilder().addExtension(CustomExtension.subjectSignTool, Boolean.valueOf(params[0]), new BERSequence(lines));
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));
        /*1.2.643.100.112*/
        put("subjectSignTool", ((certBuildContainer, params) -> {
            try {
                if (params.length == 2) {
                    certBuildContainer.getX509v3CertificateBuilder().addExtension(CustomExtension.subjectSignTool, Boolean.valueOf(params[0]), new DERUTF8String(params[1]));
                    return true;
                } else if (params.length > 2) {
                    ASN1EncodableVector lines = new ASN1EncodableVector();
                    for (int i = 1; i < params.length; i++) {
                        lines.add(new DERUTF8String(params[i]));
                    }
                    certBuildContainer.getX509v3CertificateBuilder().addExtension(CustomExtension.subjectSignTool, Boolean.valueOf(params[0]), new BERSequence(lines));
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));
        /*-------------------*/
        put("keyUsage", ((certBuildContainer, params) -> {
            //fixme тут надо как-то сделать возможность задания вручную расширений
            try {
                //if (s instanceof String[]) {
                if (params.length == 2) {
                    KeyUsage usage = new KeyUsage(Integer.parseInt(params[1], 16));
                    certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.keyUsage, Boolean.valueOf(params[0]), usage.getEncoded());
                    return true;
                }
                //  }
                //  KeyUsage usage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.keyAgreement | KeyUsage.keyCertSign | KeyUsage.cRLSign);
                // certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.keyUsage, true, usage.getEncoded());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));
                       /*  Certificate Policy:
        signToolClassKC1 (1.2.643.100.113.1)
        signToolClassKC2 (1.2.643.100.113.2)
        */
        put("certificatePolicies", ((certBuildContainer, params) -> {
            try {
                if (params.length >= 2) {
                    ASN1EncodableVector policyExtensions = new ASN1EncodableVector();
                    for (int i = 1; i < params.length; i++) {
                        policyExtensions.add(new PolicyInformation(new ASN1ObjectIdentifier(params[i])));
                    }
                    certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.certificatePolicies, Boolean.valueOf(params[0]), new DERSequence(policyExtensions));
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));
        //1.3.6.1.5.5.7.1.1 Доступ к сведениям центрах сертификации
        put("authorityInfoAccess", ((certBuildContainer, params) -> {
            //  if (ca == null) return false; //корневому сертификату это не нужно
            try {
                if (params.length == 3) {
                    ASN1EncodableVector vec = new ASN1EncodableVector();
                    vec.add(new AccessDescription(AccessDescription.id_ad_ocsp, new GeneralName(GeneralName.uniformResourceIdentifier, params[1])));
                    vec.add(new AccessDescription(AccessDescription.id_ad_caIssuers, new GeneralName(GeneralName.uniformResourceIdentifier, params[2])));
                    AuthorityInformationAccess authorityInformationAccess = AuthorityInformationAccess.getInstance(new DERSequence(vec));
                    certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.authorityInfoAccess, Boolean.valueOf(params[0]), authorityInformationAccess);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));
        //2.5.29.35 данные о вышестоящем сертификате
        put("authorityKeyIdentifier", ((certBuildContainer, params) -> {
            //  if (ca == null) return false; //корневому сертификату это не нужно
            try {
                if (params.length == 1) {
                    if (certBuildContainer.getCaCert() == null) {
                        throw new NullPointerException("Если есть authorityKeyIdentifier, то должен быть УЦ");
                    }
                    GeneralName generalName = new GeneralName(new X500Name(certBuildContainer.getCaCert().getSubjectX500Principal().getName(X500Principal.RFC2253)));

                    GeneralNames generalNames = new GeneralNames(generalName);

                    certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.authorityKeyIdentifier, Boolean.valueOf(params[0]),
                            new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(certBuildContainer.getCaCert().getPublicKey(), generalNames, certBuildContainer.getCaCert().getSerialNumber()));
                    return true;
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return false;
        }));
        //2.5.29.31 – Список отзывов
        put("cRLDistributionPoints", ((certBuildContainer, params) -> {
            //  if (ca == null) return false; //корневому сертификату это не нужно
            try {
                DistributionPoint[] distPoints = new DistributionPoint[params.length - 1];
                for (int i = 1; i < params.length; i++) {
                    DistributionPointName distributionPoint = new DistributionPointName(new GeneralNames(new GeneralName(GeneralName.uniformResourceIdentifier, params[i])));
                    distPoints[i - 1] = new DistributionPoint(distributionPoint, null, null);
                }
                System.out.println("добавление списка отзывов " + Extension.cRLDistributionPoints);
                certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.cRLDistributionPoints, Boolean.valueOf(params[0]), new CRLDistPoint(distPoints));
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));
    }
}
