package svkreml.krekerCa.signer;

import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.BCECGOST3410_2012PrivateKey;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;

import java.security.*;

public class LowLevelSignatureByHash {

    public static byte[] sign(PrivateKey privateKey, byte[] data) throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException {
        Signature signature = Signature.getInstance(CryptoAlgFactory.getKeyAlg(privateKey) +"-HASH", "BC");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public static boolean verify(PublicKey publicKey, byte[] data, byte[] sign) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(CryptoAlgFactory.getKeyAlg(publicKey)+"-HASH", "BC");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }
}
