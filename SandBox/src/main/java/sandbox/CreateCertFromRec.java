package sandbox;

import caJava.Utils.MeUtils;
import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.core.extensions.CertBuildContainer;
import caJava.core.extensions.ExtensionParam;
import caJava.core.extensions.ExtensionsHashMap;
import caJava.core.wrapper.ExtensionsMap;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;

import certDublicator.CertDublicator;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.Base64;
import java.util.Date;
import java.util.Vector;


public class CreateCertFromRec {
    static CryptoAlg cryptoAlg = CryptoAlgGost2012_256.getCryptoAlg();

    public static void main(String[] args) throws IOException, InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, OperatorCreationException {

        //byte[] reqBytes = FileManager.read(new File("C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\46230a85.000\\uc38.req"));
        byte[] reqBytes = FileManager.read(new File("C:\\Users\\s.kremlev\\Desktop\\LoadTest000-019\\Load test\\request009.req"));
        byte[] cerCa = FileManager.read(new File("C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\46230a85.000\\CA.cer"));
        X509Certificate certificateCa = CertEnveloper.decodeCert(cerCa);
        PrivateKey caPrivate = CertEnveloper.decodePrivateKey(new File("C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\46230a85.000\\CA.cer.pkey"));

        byte[] decode = new byte[0];
        try {
            decode = Base64.getMimeDecoder().decode(reqBytes);
        } catch (Exception e) {
            e.printStackTrace();
            decode=reqBytes;
        }
        ASN1InputStream asn1InputStream = new ASN1InputStream(decode);
        ASN1Primitive asn1Primitive = asn1InputStream.readObject();

        PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(asn1Primitive.getEncoded());

        X509Certificate x509Certificate = sign(certificateCa, caPrivate, pkcs10CertificationRequest);
        FileManager.write(new File("cer_sub.der"), MeUtils.concatBytes(CertEnveloper.encodeCert(x509Certificate)));
    }

    public static X509Certificate sign(X509Certificate certificateCa, PrivateKey caPrivate, PKCS10CertificationRequest pkcs10CertificationRequest)
            throws IOException, OperatorCreationException, CertificateException, NoSuchProviderException {
        Date beforeDate = new Date(System.currentTimeMillis());
        Date untilDate = new Date(System.currentTimeMillis() + 30 * 365 * 24 * 60 * 60 * 1000);
        BigInteger certSerial = new BigInteger("1");





        X500Name IssuerX500name = new JcaX509CertificateHolder(certificateCa).getSubject();
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(certificateCa.getPublicKey().getEncoded());

        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
                IssuerX500name,
                certSerial,
                beforeDate,
                untilDate,
                pkcs10CertificationRequest.getSubject(),
                keyInfo);

/*        for (Attribute attribute : pkcs10CertificationRequest.getAttributes(new ASN1ObjectIdentifier("1.2.840.113549.1.9.14"))) {
            for (ASN1Encodable attributeValue : attribute.getAttributeValues()) {
                ASN1Encodable[] extensions = ((DERSequence) attributeValue).toArray();
                for (ASN1Encodable extension : extensions) {
                    ASN1Primitive asn1Primitive = JcaX509ExtensionUtils.parseExtensionValue(((DERSequence) attributeValue).getEncoded());
                    KeyUsage keyUsage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign);


                }      X509Extension extension = new X509Extension(true, new DEROctetString(keyUsage));
                builder.

                byte[] extensionValue = attributeValue.toASN1Primitive().getEncoded();

                //    ASN1OctetString extOctetString = ASN1OctetString.getInstance(extensionValue);

                //     builder.addExtension(new ASN1ObjectIdentifier(s), true, X509ExtensionUtil.fromExtensionValue(extensionValue));
            }
        }*/
        SubjectPublicKeyInfo pkInfo = pkcs10CertificationRequest.getSubjectPublicKeyInfo();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PublicKey pubKey = converter.getPublicKey(pkInfo);
        X509Certificate donorCert = CertEnveloper.decodeCert(FileManager.read(new File("C:\\Users\\s.kremlev\\Documents\\Projects\\Other\\KrekerCA\\cerDonor.cer")));
        CertBuildContainer buildContainer = new CertBuildContainer(builder, pubKey, certificateCa,beforeDate, untilDate);
        CertDublicator.parseCertExtensions(donorCert, buildContainer);
 /*
        Vector<ExtensionParam> extensions = ExtensionsMap.getVector(extensions);

     ExtensionsHashMap extensionsHashMap = new ExtensionsHashMap();

        SubjectPublicKeyInfo pkInfo = pkcs10CertificationRequest.getSubjectPublicKeyInfo();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PublicKey pubKey = converter.getPublicKey(pkInfo);

        CertBuildContainer buildContainer = new CertBuildContainer(builder, pubKey, certificateCa);
        for (ExtensionParam extension : extensions) {
            extensionsHashMap.get(extension.name).apply(buildContainer, extension.params);
        }
*/

        //Тут начинается подготовка к подписанию и подписание
        ContentSigner sigGen = new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm)
                .build(caPrivate);
        X509CertificateHolder holder = builder.build(sigGen);
        Certificate eeX509CertificateStructure = holder.toASN1Structure();

        CertificateFactory cf = CertificateFactory.getInstance(cryptoAlg.certificateType, cryptoAlg.cryptoProvider);

        // Read Certificate
        InputStream is1 = new ByteArrayInputStream(eeX509CertificateStructure.getEncoded());
        X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
        is1.close();
        return theCert;
        //return null;
    }
}
