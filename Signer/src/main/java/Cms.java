import caJava.core.CertAndKey;
import caJava.core.cryptoAlg.CryptoAlgFactory;
import caJava.fileManagement.FileManager;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Cms {

    public static CMSSignedData sign(byte[] input, CertAndKey certAndKey, boolean encapsulate) throws InvalidKeyException, SignatureException, NoSuchProviderException, NoSuchAlgorithmException, CertificateEncodingException, OperatorCreationException, CMSException {

        //Sign
        PrivateKey privateKey = certAndKey.getPrivateKey();
        Signature signature = Signer.getSignature(input, privateKey);

        //Build CMS
        X509Certificate cert = certAndKey.getCertificate();
        CMSTypedData msg = new CMSProcessableByteArray(signature.sign());

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner signer = new JcaContentSignerBuilder(CryptoAlgFactory.getInstance(privateKey.getAlgorithm()).signatureAlgorithm).setProvider("BC").build(privateKey);
        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(signer, cert));


        gen.addCertificates(new JcaCertStore(Arrays.asList(certAndKey.getCertificateChain())));
        CMSSignedData sigData = gen.generate(msg, encapsulate);


        return sigData;
    }



}
