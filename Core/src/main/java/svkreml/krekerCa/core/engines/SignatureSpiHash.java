package svkreml.krekerCa.core.engines;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECGOST3410Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost.BCECGOST3410PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.BCECGOST3410_2012PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.GOST3410Util;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.interfaces.GOST3410Key;
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

public class SignatureSpiHash
    extends java.security.SignatureSpi
    implements PKCSObjectIdentifiers, X509ObjectIdentifiers
{
    private static final int DIGEST_SIZE = 32;
    private byte[] hash;
   // private Digest                  digest;
    private DSA                     signer;

    public SignatureSpiHash()
    {
       // this.digest = new GOST3411Digest();
        this.signer = new ECGOST3410Signer();
    }

    protected void engineInitVerify(
        PublicKey   publicKey)
        throws InvalidKeyException
    {
        CipherParameters    param;

        if (publicKey instanceof ECPublicKey)
        {
            try {
                param = (ECKeyParameters) generatePublicKeyParameter(publicKey);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new InvalidKeyException(e);
            }
        }
        else if (publicKey instanceof GOST3410Key)
        {
            param = GOST3410Util.generatePublicKeyParameter(publicKey);
        }
        else
        {
            try
            {
                byte[]  bytes = publicKey.getEncoded();

                publicKey = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(bytes));

                param = ECUtil.generatePublicKeyParameter(publicKey);
            }
            catch (Exception e)
            {
                throw new InvalidKeyException("can't recognise key type in DSA based signer");
            }
        }

       // digest.reset();
        signer.init(false, param);
    }

    protected void engineInitSign(
        PrivateKey  privateKey)
        throws InvalidKeyException
    {
        CipherParameters    param;

        if (privateKey instanceof ECKey)
        {
            param = ECUtil.generatePrivateKeyParameter(privateKey);
        }
        else
        {
            param = GOST3410Util.generatePrivateKeyParameter(privateKey);
        }

       // digest.reset();

        if (appRandom != null)
        {
            signer.init(true, new ParametersWithRandom(param, appRandom));
        }
        else
        {
            signer.init(true, param);
        }
    }

    protected void engineUpdate(
            byte    b)
            throws SignatureException
    {
        throw new SignatureException("not implemented");
        // digest.update(b);
    }

    protected void engineUpdate(
            byte[]  b,
            int     off,
            int     len)
            throws SignatureException
    {
        hash = Arrays.copyOfRange(b, off, len);
        if(hash.length != DIGEST_SIZE)
            throw new SignatureException("DIGEST_SIZE must be "+ DIGEST_SIZE);
        // digest.update(b, off, len);
    }

    protected byte[] engineSign()
        throws SignatureException
    {
       // byte[]  hash = new byte[digest.getDigestSize()];

       // digest.doFinal(hash, 0);

        try
        {
            byte[]          sigBytes = new byte[64];
            BigInteger[]    sig = signer.generateSignature(hash);
            byte[]          r = sig[0].toByteArray();
            byte[]          s = sig[1].toByteArray();

            if (s[0] != 0)
            {
                System.arraycopy(s, 0, sigBytes, 32 - s.length, s.length);
            }
            else
            {
                System.arraycopy(s, 1, sigBytes, 32 - (s.length - 1), s.length - 1);
            }
            
            if (r[0] != 0)
            {
                System.arraycopy(r, 0, sigBytes, 64 - r.length, r.length);
            }
            else
            {
                System.arraycopy(r, 1, sigBytes, 64 - (r.length - 1), r.length - 1);
            }

            return sigBytes;
        }
        catch (Exception e)
        {
            throw new SignatureException(e.toString());
        }
    }
    
    protected boolean engineVerify(
        byte[]  sigBytes) 
        throws SignatureException
    {
       // byte[]  hash = new byte[digest.getDigestSize()];

       // digest.doFinal(hash, 0);

        BigInteger[]    sig;

        try
        {
            byte[] r = new byte[32]; 
            byte[] s = new byte[32];

            System.arraycopy(sigBytes, 0, s, 0, 32);

            System.arraycopy(sigBytes, 32, r, 0, 32);
            
            sig = new BigInteger[2];
            sig[0] = new BigInteger(1, r);
            sig[1] = new BigInteger(1, s);
        }
        catch (Exception e)
        {
            throw new SignatureException("error decoding signature bytes.");
        }

        return signer.verifySignature(hash, sig[0], sig[1]);
    }

    protected void engineSetParameter(
        AlgorithmParameterSpec params)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated replaced with #engineSetParameter(java.security.spec.AlgorithmParameterSpec)
     */
    protected void engineSetParameter(
        String  param,
        Object  value)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated
     */
    protected Object engineGetParameter(
        String      param)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    static AsymmetricKeyParameter generatePublicKeyParameter(
            PublicKey key)
            throws InvalidKeyException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method engineGetKeyParametersMethod = BCECGOST3410PublicKey.class.getDeclaredMethod("engineGetKeyParameters");
        engineGetKeyParametersMethod.setAccessible(true);
        return (key instanceof BCECGOST3410PublicKey) ?
                (AsymmetricKeyParameter) engineGetKeyParametersMethod.invoke(key)
                : ECUtil.generatePublicKeyParameter(key);
    }
}
