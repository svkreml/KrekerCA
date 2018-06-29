package sandbox;

import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
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
    static CryptoAlg cryptoAlg = CryptoAlgGost2001.getCryptoAlg();

    public static void main(String[] args) throws IOException, InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, OperatorCreationException {

        byte[] bytes = FileManager.read(new File("rec1.pkcs10"));
        byte[] cerCa = FileManager.read(new File("cer.der"));
        X509Certificate certificateCa = CertEnveloper.decodeCert(cerCa);
        PKCS10CertificationRequest pkcs10CertificationRequest = CertEnveloper.decodeCertRec(bytes);

        // create keypair
        KeyPairGenerator keypairGen = KeyPairGenerator.getInstance(cryptoAlg.algorithm, cryptoAlg.cryptoProvider);
        keypairGen.initialize(new ECGenParameterSpec(cryptoAlg.ellipticCurve));
        KeyPair keypair = keypairGen.generateKeyPair();


        PrivateKey caPrivate = null;
        X509Certificate x509Certificate =  sign(caPrivate, keypair, bytes);

    }

    public static X509Certificate sign(PrivateKey caPrivate, KeyPair pair, byte[] bytes)
            throws IOException, OperatorCreationException, CertificateException, NoSuchProviderException {

        PKCS10CertificationRequest inputCSR = new PKCS10CertificationRequest(bytes);

        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
                .find(cryptoAlg.algorithm);
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder()
                .find(sigAlgId);

        AsymmetricKeyParameter foo = PrivateKeyFactory.createKey(caPrivate
                .getEncoded());
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(pair
                .getPublic().getEncoded());


        PKCS10CertificationRequest pk10Holder = new PKCS10CertificationRequest(CertificationRequest.getInstance(inputCSR));

        X509v3CertificateBuilder myCertificateGenerator = new X509v3CertificateBuilder(
                new X500Name("CN=issuer"), new BigInteger("1"), new Date(
                System.currentTimeMillis()), new Date(
                System.currentTimeMillis() + 30 * 365 * 24 * 60 * 60
                        * 1000), pk10Holder.getSubject(), keyInfo);

        ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
                .build(foo);

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
