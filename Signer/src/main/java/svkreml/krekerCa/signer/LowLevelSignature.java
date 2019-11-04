package svkreml.krekerCa.signer;

import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;

import java.io.InputStream;
import java.security.*;

public class LowLevelSignature {
    public static byte[] sign(PrivateKey privateKey, byte[] data) throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException {
        Signature signature = Signature.getInstance(CryptoAlgFactory.getKeyAlg(privateKey), "BC");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public static boolean verify(PublicKey publicKey, byte[] data, byte[] sign) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(CryptoAlgFactory.getKeyAlg(publicKey), "BC");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }

    public static byte[] sign(PrivateKey privateKey, InputStream input) throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException {
        return null;
    }

    public static boolean verify(PublicKey publicKey, InputStream input, byte[] sign) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return false;
    }


}
