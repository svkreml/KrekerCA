package svkreml.krekerCa.core.cryptoAlg;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoAlgGost2001;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoAlgGost2012_512;
import svkreml.krekerCa.core.cryptoAlg.impl.CryptoRSA;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
public class CryptoAlgFactory {

    public static String getKeyAlg(PrivateKey privateKey) throws NoSuchAlgorithmException {
        switch (PrivateKeyInfo.getInstance(privateKey.getEncoded()).getPrivateKeyAlgorithm().getAlgorithm().getId()) {
            case "1.2.643.2.2.19":
                return "GOST3411withECGOST3410";
            case "1.2.643.7.1.1.1.1":
                return "GOST3411-2012-256WITHECGOST3410-2012-256";
            case "1.2.643.7.1.1.1.2":
                return "GOST3411-2012-512WITHECGOST3410-2012-512";
            default:
                throw new NoSuchAlgorithmException("Не найден алгортитм по данному OID: " +
                        PrivateKeyInfo.getInstance(privateKey.getEncoded()).getPrivateKeyAlgorithm().getAlgorithm().getId());
        }
    }

    public static String getKeyAlg(PublicKey publicKey) throws NoSuchAlgorithmException {

        switch (SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()).getAlgorithm().getAlgorithm().getId()) {
            case "1.2.643.2.2.19":
                return "GOST3411withECGOST3410";
            case "1.2.643.7.1.1.1.1":
                return "GOST3411-2012-256WITHECGOST3410-2012-256";
            case "1.2.643.7.1.1.1.2":
                return "GOST3411-2012-512WITHECGOST3410-2012-512";
            default:
                throw new NoSuchAlgorithmException("Не найден алгортитм по данному OID: " +
                        PrivateKeyInfo.getInstance(publicKey.getEncoded()).getPrivateKeyAlgorithm().getAlgorithm().getId());

        }
    }

        /*caCert.getSigAlgName()*/
        public static CryptoAlg getInstance (String alg) throws NoSuchAlgorithmException {
            switch (alg) {
                case "1.2.643.2.2.3":
                    log.info("Определён алгорит ключа как ГОСТ 2001: " + alg);
                    return CryptoAlgGost2001.getCryptoAlg();
                case "1.2.643.7.1.1.3.2":
                    log.info("Определён алгорит ключа как ГОСТ 2012 256: " + alg);
                    return CryptoAlgGost2012_256.getCryptoAlg();
                case "1.2.643.7.1.1.3.3":
                    log.info("Определён алгорит ключа как ГОСТ 2012 512: " + alg);
                    return CryptoAlgGost2012_512.getCryptoAlg();
                case "SHA256withRSA":
                    log.info("Определён алгорит ключа как RSA: " + alg);
                    return CryptoRSA.getCryptoAlg(2048);
                case "???"://fixme rsa =2012_512
                    return CryptoRSA.getCryptoAlg(2048);
                default:
                    log.error("Алгоритм ключа не определён: " + alg);
                    throw new NoSuchAlgorithmException("Не найден алгортитм по данному OID: " + alg);
            }
        }
    }
