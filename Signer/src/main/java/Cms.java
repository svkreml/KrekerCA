import caJava.core.CertAndKey;
import caJava.core.cryptoAlg.CryptoAlgFactory;
import caJava.fileManagement.FileManager;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

//https://stackoverflow.com/questions/16662408/correct-way-to-sign-and-verify-signature-using-bouncycastle
public class Cms {

/*
    public static CMSSignedData sign(byte[] input, CertAndKey certAndKey, boolean encapsulate) throws InvalidKeyException, SignatureException, NoSuchProviderException, NoSuchAlgorithmException, CertificateEncodingException, OperatorCreationException, CMSException {

        //Sign
        PrivateKey privateKey = certAndKey.getPrivateKey();
        Signature signature = SimpleSignature.getSignature(input, privateKey);

        //Build CMS
        X509Certificate cert = certAndKey.getCertificate();
        CMSTypedData msg = new CMSProcessableByteArray(signature.sign());
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner signer = new JcaContentSignerBuilder(CryptoAlgFactory.getInstance(privateKey.getAlgorithm()).signatureAlgorithm).setProvider("BC").build(privateKey);
        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(signer, cert));
        gen.addCertificates(new JcaCertStore(Arrays.asList(certAndKey.getCertificateChain())));
        return gen.generate(msg, encapsulate);
    }
*/


    public static Object getContent(byte[] cms) throws CMSException {
        Object content = new CMSSignedData(cms).getSignedContent().getContent();
        return content;
    }
//FIXME не работает
    public static void verifyByHash(byte[] bytes, byte[] hash) throws CMSException, CertificateException, OperatorCreationException {
        CMSSignedData cms;
        String myHash = "9D81CF727B4A63E4FBD312BF755CFB64701AC4C5FC49DA55302A10059BBCCACA";
        String cryptoProHash = "D918FC27B7A4364EBF3D21FB57C5BF4607A14C5CCF94AD5503A20150B9CBACAC";
        //String cryptoProHash = "b01fae8272c6c6be5bf9e3e85d8f50563f42c22167ed4e88f0b029e31e069bec";
        byte[] read = new byte[0];
        try {
            read = FileManager.read(new File("C:\\Users\\svkre\\Downloads\\cms-det-hach_2001_2018.sig"));
            System.out.println(Hex.toHexString(read));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap hashes = new HashMap();
        hashes.put("1.2.643.2.2.9", read);
        // hashes.put("1.2.643.2.2.9", Hex.decode(myHash));

        cms = new CMSSignedData(hashes, bytes);
        //cms =  new CMSSignedData( bytes);
/*        SignerInformation signerInformation = cms.getSignerInfos().getSigners().iterator().next();
        AttributeTable attributes = signerInformation.getSignedAttributes();
        Attribute attribute = attributes.get(CMSAttributes.messageDigest);
        DEROctetString digest = (DEROctetString) attribute.getAttrValues().getObjectAt(0);
 //if these values are different, the exception is thrown
        System.out.println("digest.getOctets()                   "+Hex.toHexString(digest.getOctets()));
        System.out.println("signerInformation.getContentDigest() "+Hex.toHexString(signerInformation.getContentDigest()));*/

        Store store = cms.getCertificates();
        SignerInformationStore signers = cms.getSignerInfos();
        Collection c = signers.getSigners();
        Iterator it = c.iterator();

        verifyEverySign(store, it);

    }

    private static void verifyEverySign(Store store, Iterator it) throws CertificateException, OperatorCreationException, CMSException {
        while (it.hasNext()) {
            SignerInformation signer = (SignerInformation) it.next();
            Collection certCollection = store.getMatches(signer.getSID());
            Iterator certIt = certCollection.iterator();
            X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
            SignerInformationVerifier signerInformationVerifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert);
            if (signer.verify(signerInformationVerifier)) {
                System.out.println("verified");
            } else
                System.out.println("sign wrong");
         }



    }

    public static void verifyAttached(byte[] bytes) throws CMSException, CertificateException, OperatorCreationException {
        CMSSignedData cms;

        // attached constructor
        cms = new CMSSignedData(bytes);

        Store store = cms.getCertificates();
        SignerInformationStore signers = cms.getSignerInfos();
        Collection c = signers.getSigners();
        Iterator it = c.iterator();
        verifyEverySign(store, it);

    }

    public static void verifyDetached(byte[] bytes, byte[] originalContent) throws CMSException, CertificateException, OperatorCreationException {
        CMSSignedData cms;

        CMSProcessable signedContent = new CMSProcessableByteArray(originalContent);
        InputStream is = new ByteArrayInputStream(bytes);
        cms = new CMSSignedData(signedContent, is);

        Store store = cms.getCertificates();
        SignerInformationStore signers = cms.getSignerInfos();
        Collection c = signers.getSigners();
        Iterator it = c.iterator();
        verifyEverySign(store, it);

    }

    public static CMSSignedData generateEnvelopedSignature(byte[] signatureFileBytes,
                                                           CertAndKey certAndKey, boolean isDetached)
            throws CertificateEncodingException, SignatureException {
        JcaCertStore certs = new JcaCertStore(Arrays.asList(certAndKey.getCertificateChain()));
        X509Certificate signerCert = certAndKey.getCertificate();
        try {
            ContentSigner signer =
                    new JcaContentSignerBuilder(CryptoAlgFactory.getInstance(certAndKey.getPrivateKey().getAlgorithm()).signatureAlgorithm).setProvider("BC").build(certAndKey.getPrivateKey());
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            gen.addSignerInfoGenerator(
                    new SignerInfoGeneratorBuilder(
                            new JcaDigestCalculatorProviderBuilder().build())
                            .setDirectSignature(true)
                            .build(signer, new JcaX509CertificateHolder(signerCert)));
            gen.addCertificates(certs);

            CMSSignedData sigData =
                    gen.generate(new CMSProcessableByteArray(signatureFileBytes), !isDetached);

            System.out.println("rawSignature = " + new String(Base64.encode(sigData.getSignerInfos().getSigners().iterator().next().getSignature())));
            return sigData;
        } catch (OperatorCreationException | CMSException e) {
            throw new SignatureException("Failed to generate signature", e);
        }
    }
}
