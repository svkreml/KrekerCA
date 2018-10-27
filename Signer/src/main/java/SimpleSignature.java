import caJava.Utils.MeUtils;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;

import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;


/*
* Почему-то подпись делается неправильно
* */
public class SimpleSignature {
    public static Signature getSignature(byte[] input, PrivateKey privKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        String algorithm = privKey.getAlgorithm();
        System.out.println("algorithm = " + algorithm);
        Signature signature = Signature.getInstance(algorithm, "BC");
      //  AlgorithmParameterSpec params = new AEADParameterSpec();
      //  signature.setParameter(params);
        signature.initSign(privKey);
        signature.update(input);
        return signature;
    }

    public static boolean validateSignature(byte[] input, byte[] rawSignature, X509Certificate x509Certificate) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String algorithm = x509Certificate.getPublicKey().getAlgorithm();
        System.out.println("algorithm = " + algorithm);
       // byte[] reverse = Arrays.reverse(input);
        Signature signature = Signature.getInstance(algorithm, "BC");
        signature.initVerify(x509Certificate.getPublicKey());
        signature.update(input);
        return signature.verify(rawSignature);

    }


}
