import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

public class OcspServer {

    private OCSPResp generateOCSPResponse(OCSPReq request, PrivateKey ocspPrivateKey, PublicKey ocspPublicKey,
                                          CertificateID[] revokedIDs) throws NoSuchProviderException, OCSPException, IOException, OperatorCreationException {
        ASN1InputStream aIn = new ASN1InputStream(ocspPublicKey.getEncoded());
        SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(aIn.readObject());
        AlgorithmIdentifier algorithmIdentifier = info.getAlgorithm();

        SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, ocspPublicKey.getEncoded());

        JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
        DigestCalculatorProvider digestCalculatorProvider = digestCalculatorProviderBuilder.build();

        DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);

        BasicOCSPRespBuilder basicOCSPRespGenerator = new BasicOCSPRespBuilder(subjectPublicKeyInfo, digestCalculator);

        Extension extension = request.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        if (extension != null) {
            ExtensionsGenerator extGen = new ExtensionsGenerator();
            extGen.addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, extension);
            basicOCSPRespGenerator.setResponseExtensions(extGen.generate());
        }

        Req[] requests = request.getRequestList();

        for (Req req : requests) {
            boolean revoked = false;
            CertificateID certID = req.getCertID();
            for (CertificateID revokedID : revokedIDs) {
                if (certID.equals(revokedID)){
                    revoked = true;
                }
            }
            if (revoked) {
                RevokedStatus revokedStatus = new RevokedStatus(new Date(), CRLReason.privilegeWithdrawn);
                Date nextUpdate = new Date(new Date().getTime() + 10_000_000);
                basicOCSPRespGenerator.addResponse(certID, revokedStatus, new Date(), nextUpdate);
                break;
            } else {
                basicOCSPRespGenerator.addResponse(certID, CertificateStatus.GOOD);
            }
        }
        // build BouncyCastle certificate
        ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BouncyCastleProvider.PROVIDER_NAME).build(ocspPrivateKey);
        BasicOCSPResp basicResp = basicOCSPRespGenerator.build(signer, null, new Date());
        OCSPRespBuilder respGen = new OCSPRespBuilder();
        return respGen.build(OCSPRespBuilder.SUCCESSFUL, basicResp);
    }
}
