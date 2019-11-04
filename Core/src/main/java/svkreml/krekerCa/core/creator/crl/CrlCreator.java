package svkreml.krekerCa.core.creator.crl;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.core.cryptoAlg.CryptoAlgFactory;
import svkreml.krekerCa.customOID.CustomBCStyle;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Builder
public class CrlCreator {
    private CertAndKey singingCertAndKey;
    private BigInteger serialNumber;
    private Date generateDate;
    private Date nextUpdate;
    private List<RevokedCertificate> revokedCertificates;

    private static void addRevokedCertInCrl(X509v2CRLBuilder builder, RevokedCertificate revokedCertificate) {
        log.info(
                "Adding record to generating crl: {}, {}, {}",
                revokedCertificate.getSerialNumber().toString(16),
                revokedCertificate.getDate(),
                revokedCertificate.getReason()
        );

        builder.addCRLEntry(revokedCertificate.getSerialNumber(),
                revokedCertificate.getDate(),
                revokedCertificate.getReason());
    }

    public X509CRL generate() throws NoSuchAlgorithmException, CertIOException, CRLException {
        log.info("begining crl generation");
        X500Name subjectX500Name = X500Name.getInstance(
                CustomBCStyle.INSTANCE,
                singingCertAndKey.getCertificate().getSubjectX500Principal().getEncoded()
        );
        X509v2CRLBuilder builder = new X509v2CRLBuilder(
                subjectX500Name,
                generateDate
        );

        builder.setNextUpdate(nextUpdate);

        builder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(
                        singingCertAndKey.getCertificate().getPublicKey(),
                        new GeneralNames(new GeneralName(subjectX500Name)),
                        singingCertAndKey.getCertificate().getSerialNumber()
                )
        );

        builder.addExtension(Extension.cRLNumber, false, new CRLNumber(BigInteger.valueOf(new Date().getTime())));

        revokedCertificates.forEach(r -> addRevokedCertInCrl(builder, r));

        JcaContentSignerBuilder contentSignerBuilder =
                new JcaContentSignerBuilder(
                        Objects.requireNonNull(CryptoAlgFactory.getInstance(
                                singingCertAndKey.getCertificate().getSigAlgName()
                        )).getSignatureAlgorithm()
                );

        contentSignerBuilder.setProvider("BC");

        X509CRLHolder crlHolder;
        try {
            crlHolder = builder.build(contentSignerBuilder.build(singingCertAndKey.getPrivateKey()));
        } catch (OperatorCreationException e) {
            throw new NoSuchAlgorithmException("Error contentSignerBuilder.build(singingCertAndKey.getPrivateKey())");
        }
        JcaX509CRLConverter converter = new JcaX509CRLConverter();
        converter.setProvider("BC");
        X509CRL x509CRL = converter.getCRL(crlHolder);
        log.info("crl generated for certificate (subject in reverse order): {}",
                X500Name.getInstance(
                        CustomBCStyle.INSTANCE, x509CRL.getIssuerX500Principal().getEncoded()
                )
        );
        return x509CRL;
    }
}

