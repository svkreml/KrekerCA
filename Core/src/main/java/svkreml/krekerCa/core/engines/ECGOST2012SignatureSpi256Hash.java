package svkreml.krekerCa.core.engines;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECGOST3410_2012Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.BCECGOST3410_2012PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

/**
 * Signature for GOST34.10 2012 256. Algorithm is the same as for GOST34.10 2001
 */
public class ECGOST2012SignatureSpi256Hash
        extends java.security.SignatureSpi
        implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
    private static final int DIGEST_SIZE = 32;
    private byte[] hash;
    //  private Digest                  digest;
    private DSA signer;
    private int size = 64;
    private int halfSize = size / 2;

    public ECGOST2012SignatureSpi256Hash() {
        //    this.digest = new GOST3411_2012_256Digest();
        this.signer = new ECGOST3410_2012Signer();
    }

    static AsymmetricKeyParameter generatePublicKeyParameter(
            PublicKey key)
            throws InvalidKeyException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method engineGetKeyParametersMethod = BCECGOST3410_2012PublicKey.class.getDeclaredMethod("engineGetKeyParameters");
        engineGetKeyParametersMethod.setAccessible(true);
        return (key instanceof BCECGOST3410_2012PublicKey) ?
                (AsymmetricKeyParameter) engineGetKeyParametersMethod.invoke(key)
                : ECUtil.generatePublicKeyParameter(key);
    }

    protected void engineInitVerify(
            PublicKey publicKey)
            throws InvalidKeyException {
        ECKeyParameters param;

        if (publicKey instanceof ECPublicKey) {
            try {
                param = (ECKeyParameters) generatePublicKeyParameter(publicKey);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new InvalidKeyException(e);
            }
        } else {
            try {
                byte[] bytes = publicKey.getEncoded();

                publicKey = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(bytes));

                param = (ECKeyParameters) ECUtil.generatePublicKeyParameter(publicKey);
            } catch (Exception e) {
                throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-256 signer");
            }
        }

        if (param.getParameters().getN().bitLength() > 256) {
            throw new InvalidKeyException("key out of range for ECGOST-2012-256");
        }

        // digest.reset();
        signer.init(false, param);
    }

    protected void engineInitSign(
            PrivateKey privateKey)
            throws InvalidKeyException {
        ECKeyParameters param;

        if (privateKey instanceof ECKey) {
            param = (ECKeyParameters) ECUtil.generatePrivateKeyParameter(privateKey);
        } else {
            throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-256 signer");
        }

        if (param.getParameters().getN().bitLength() > 256) {
            throw new InvalidKeyException("key out of range for ECGOST-2012-256");
        }

        // digest.reset();

        if (appRandom != null) {
            signer.init(true, new ParametersWithRandom(param, appRandom));
        } else {
            signer.init(true, param);
        }
    }

    protected void engineUpdate(
            byte b)
            throws SignatureException {
        throw new SignatureException("not implemented");
        // digest.update(b);
    }

    protected void engineUpdate(
            byte[] b,
            int off,
            int len)
            throws SignatureException {
        hash = Arrays.copyOfRange(b, off, len);
        if (hash.length != DIGEST_SIZE)
            throw new SignatureException("DIGEST_SIZE must be " + DIGEST_SIZE);
        // digest.update(b, off, len);
    }

    protected byte[] engineSign()
            throws SignatureException {
        // byte[]  hash = new byte[DIGEST_SIZE];

        // digest.doFinal(hash, 0);

        try {
            byte[] sigBytes = new byte[size];
            BigInteger[] sig = signer.generateSignature(hash);
            byte[] r = sig[0].toByteArray();
            byte[] s = sig[1].toByteArray();

            if (s[0] != 0) {
                System.arraycopy(s, 0, sigBytes, halfSize - s.length, s.length);
            } else {
                System.arraycopy(s, 1, sigBytes, halfSize - (s.length - 1), s.length - 1);
            }

            if (r[0] != 0) {
                System.arraycopy(r, 0, sigBytes, size - r.length, r.length);
            } else {
                System.arraycopy(r, 1, sigBytes, size - (r.length - 1), r.length - 1);
            }

            return sigBytes;
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    protected boolean engineVerify(
            byte[] sigBytes)
            throws SignatureException {
        // byte[]  hash = new byte[DIGEST_SIZE];

        // digest.doFinal(hash, 0);

        BigInteger[] sig;

        try {
            byte[] r = new byte[halfSize];
            byte[] s = new byte[halfSize];

            System.arraycopy(sigBytes, 0, s, 0, halfSize);

            System.arraycopy(sigBytes, halfSize, r, 0, halfSize);

            sig = new BigInteger[2];
            sig[0] = new BigInteger(1, r);
            sig[1] = new BigInteger(1, s);
        } catch (Exception e) {
            throw new SignatureException("error decoding signature bytes.");
        }

        return signer.verifySignature(hash, sig[0], sig[1]);
    }

    protected void engineSetParameter(
            AlgorithmParameterSpec params) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated replaced with "#engineSetParameter(java.security.spec.AlgorithmParameterSpec)"
     */
    protected void engineSetParameter(
            String param,
            Object value) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated
     */
    protected Object engineGetParameter(
            String param) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
}
