package svkreml.krekerCa.signer;

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
import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SimpleCmsSignature {
    public static CMSSignedData sign(CertAndKey certAndKey,
                                     byte[] data, Type type)
            throws CertificateEncodingException, SignatureException {
       if(type == Type.BY_HASH) throw new RuntimeException("Не реализовано!");
        JcaCertStore certs = new JcaCertStore(Arrays.asList(certAndKey.getCertificateChain()));
        X509Certificate signerCert = certAndKey.getCertificate();
        try {
            ContentSigner signer =
                    new JcaContentSignerBuilder(
                            CryptoAlgFactory.getKeyAlg(certAndKey.getPrivateKey())
                               /*     + (type == Type.BY_HASH ? "-HASH": "")*/
                    ).setProvider("BC").build(certAndKey.getPrivateKey());
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            gen.addSignerInfoGenerator(
                    new SignerInfoGeneratorBuilder(
                            new JcaDigestCalculatorProviderBuilder().build())
                            .setDirectSignature(true)
                            .build(signer, new JcaX509CertificateHolder(signerCert)));
            gen.addCertificates(certs);
            return gen.generate(new CMSProcessableByteArray(data), type == Type.ATTACHED);
        } catch (OperatorCreationException | CMSException | NoSuchAlgorithmException e) {
            throw new SignatureException("Failed to generate signature", e);
        }
    }

    private static boolean verifyEverySign(Store<X509CertificateHolder> store, Iterator<SignerInformation> it) throws CertificateException, OperatorCreationException, CMSException {
        boolean isValid = true;
        while (it.hasNext()) {
            SignerInformation signer = it.next();
            @SuppressWarnings("unchecked")
            Collection<X509CertificateHolder> certCollection = (Collection<X509CertificateHolder>) store.getMatches(signer.getSID());
            X509CertificateHolder certHolder = certCollection.iterator().next();
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
            SignerInformationVerifier signerInformationVerifier = new JcaSimpleSignerInfoVerifierBuilder()
                    .setProvider("BC").build(cert);
            if (!signer.verify(signerInformationVerifier)) {
                isValid = false;
            }
        }
        return isValid;
    }

    public static boolean verifyAttached(byte[] signature) throws CMSException, CertificateException, OperatorCreationException {
        // attached constructor
        CMSSignedData cms = new CMSSignedData(signature);
        Store<X509CertificateHolder> store = cms.getCertificates();
        SignerInformationStore signers = cms.getSignerInfos();
        Collection<SignerInformation>  c = signers.getSigners();
        Iterator<SignerInformation> it = c.iterator();
        return verifyEverySign(store, it);

    }

    public static boolean verifyDetached(byte[] signature, byte[] originalContent) throws CMSException, CertificateException, OperatorCreationException {
        CMSProcessable signedContent = new CMSProcessableByteArray(originalContent);
        InputStream is = new ByteArrayInputStream(signature);
        CMSSignedData cms = new CMSSignedData(signedContent, is);
        Store<X509CertificateHolder> store = cms.getCertificates();
        SignerInformationStore signers = cms.getSignerInfos();
        Collection<SignerInformation> c = signers.getSigners();
        Iterator<SignerInformation> it = c.iterator();
        return  verifyEverySign(store, it);
    }

    public static boolean verifyByHash(byte[] signature, byte[] digest) {
        throw new RuntimeException("Не реализовано!");
        /*        // CMSProcessable signedContent = new CMSProcessableByteArray(digest);
        // InputStream is = new ByteArrayInputStream(signature);
        // CMSSignedData cms = new CMSSignedData(signedContent, is);
        HashMap<String, byte[]> hashes = new HashMap<>();

        hashes.put("1.2.643.2.2.9", digest);

        CMSSignedData cms = new CMSSignedData(hashes, signature);

// sigData.getSignerInfos().getSigners().iterator().next().getSignature()
        Store<X509CertificateHolder> store = cms.getCertificates();
        Iterator<SignerInformation> it = cms.getSignerInfos().getSigners().iterator();
        return  verifyEverySign(store, it);*/
    }

    enum Type {
        ATTACHED,
        DETACHED,
        BY_HASH
    }
}
