package sandbox;

import caJava.Utils.MeUtils;
import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
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
import java.security.spec.ECGenParameterSpec;
import java.util.Date;


public class CreateCertFromRec {
    static CryptoAlg cryptoAlg = CryptoAlgGost2012_256.getCryptoAlg();

    public static void main(String[] args) throws IOException, InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, OperatorCreationException {

        byte[] reqBytes = FileManager.read(new File("C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\46230a85.000\\uc38.req"));
        byte[] cerCa = FileManager.read(new File("C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\46230a85.000\\CA.cer"));
        X509Certificate certificateCa = CertEnveloper.decodeCert(cerCa);
       PrivateKey caPrivate = CertEnveloper.decodePrivateKey(new File("C:\\Users\\s.kremlev\\Desktop\\gost-export-cryptopro-key\\46230a85.000\\CA.cer.pkey"));


        ASN1InputStream asn1InputStream = new ASN1InputStream(reqBytes);
        ASN1Primitive asn1Primitive = asn1InputStream.readObject();

        PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(asn1Primitive.getEncoded());

        X509Certificate x509Certificate =  sign(certificateCa, caPrivate, pkcs10CertificationRequest);
        FileManager.write(new File("cer_sub.der"), MeUtils.concatBytes(CertEnveloper.encodeCert(x509Certificate)));
    }

    public static X509Certificate sign(X509Certificate certificateCa, PrivateKey caPrivate, PKCS10CertificationRequest pkcs10CertificationRequest)
            throws IOException, OperatorCreationException, CertificateException, NoSuchProviderException {



        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(certificateCa.getPublicKey().getEncoded());


        PKCS10CertificationRequest pk10Holder = pkcs10CertificationRequest;


        X500Name x500name = new JcaX509CertificateHolder(certificateCa).getSubject();


        X509v3CertificateBuilder myCertificateGenerator = new X509v3CertificateBuilder(
                x500name, new BigInteger("1"), new Date(
                System.currentTimeMillis()), new Date(
                System.currentTimeMillis() + 30 * 365 * 24 * 60 * 60
                        * 1000), pk10Holder.getSubject(), keyInfo);

        ContentSigner sigGen =    new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm)
                .build(caPrivate);

        X509CertificateHolder holder = myCertificateGenerator.build(sigGen);
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
