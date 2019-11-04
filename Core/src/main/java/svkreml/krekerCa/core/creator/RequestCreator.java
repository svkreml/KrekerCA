package svkreml.krekerCa.core.creator;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import svkreml.krekerCa.core.BcInit;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlg;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;
import svkreml.krekerCa.core.wrapper.SubjectMap;
import svkreml.krekerCa.fileManagement.FileManager;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

/*
 * Создание запроса на сертификат для данного УЦ и для вышестоящего.
 * */
public class RequestCreator {
    CryptoAlg cryptoAlg;

    public RequestCreator(CryptoAlg cryptoAlg) {
        this.cryptoAlg = cryptoAlg;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, OperatorCreationException {
        RequestCreator requestCreator = new RequestCreator(CryptoAlgFactory.getInstance("ECGOST3410-2012"));
        requestCreator.createReqest(SubjectMap.load(new File("ConsoleApp\\subject.json")));
    }

    //todo заполнить RequestCreator
    public void createReqest(X500Name subject) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IOException, OperatorCreationException {
        BcInit.init();

        //   SecureRandom random = new SecureRandom();

// create keypair
        KeyPairGenerator keypairGen = KeyPairGenerator.getInstance(cryptoAlg.algorithm, cryptoAlg.cryptoProvider);
        keypairGen.initialize(new ECGenParameterSpec(cryptoAlg.ellipticCurve));
        KeyPair keypair = keypairGen.generateKeyPair();


        PKCS10CertificationRequestBuilder requestBuilder = new JcaPKCS10CertificationRequestBuilder(subject, keypair.getPublic());
        ExtensionsGenerator extGen = new ExtensionsGenerator();

        String[] params = new String[]{"true", "true"};
        BasicConstraints constraints;
        if (params[1].contains("e"))
            constraints = new BasicConstraints(Boolean.valueOf(params[1]));
        else
            constraints = new BasicConstraints(Integer.parseInt(params[1]));
        extGen.addExtension(Extension.basicConstraints, Boolean.valueOf(params[0]), constraints.getEncoded());

        params = new String[]{"1.2.643.100.113.1"};
        ASN1EncodableVector policyExtensions = new ASN1EncodableVector();
        for (int i = 1; i < params.length; i++) {
            policyExtensions.add(new PolicyInformation(new ASN1ObjectIdentifier(params[i])));
        }
        extGen.addExtension(Extension.certificatePolicies, false, new DERSequence(policyExtensions));

        requestBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());


        PKCS10CertificationRequest req1 = requestBuilder.build(new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm).setProvider(cryptoAlg.cryptoProvider).build(keypair.getPrivate()));

        FileManager.write(new File("rec1.pkcs10"), req1.getEncoded());
        FileManager.write(new File("rec1.pkey"), keypair.getPrivate().getEncoded());


    }
}
