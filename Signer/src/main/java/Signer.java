import java.security.*;

public class Signer {
    public static Signature getSignature(byte[] input, PrivateKey privKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(privKey.getAlgorithm(), "BC");
        signature.initSign(privKey);
        signature.update(input);
        return signature;
    }
}
