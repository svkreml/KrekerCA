package sandbox;


import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;

import java.io.File;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

public class CreateRevocationList {
    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        byte[] cerCa = FileManager.read(new File("cer.der"));
        X509Certificate caCert = CertEnveloper.decodeCert(cerCa);
        PrivateKey caCertPrivateKey = CertEnveloper.decodePrivateKey(new File("cer.pkey"));

        byte[] cer = FileManager.read(new File("cer_t_sub_dynya.der"));
        X509Certificate cert = CertEnveloper.decodeCert(cer);


        X509CRL x509CRL = generateCrl(caCert, caCertPrivateKey, cert);
        FileManager.write(new File("src/test/TestResources/pages/revoked.crl"), x509CRL.getEncoded());
    }

    static private X509CRL generateCrl(X509Certificate ca, PrivateKey caPrivateKey, X509Certificate... revoked) throws Exception {
        CryptoAlg cryptoAlg = CryptoAlgGost2001.getCryptoAlg();


        X509v2CRLBuilder builder = new X509v2CRLBuilder(
                new X500Name(ca.getIssuerX500Principal().getName()),
                new Date()
        );
        builder.setNextUpdate(Date.from(LocalDate.of(2018, 3, 15).atStartOfDay(ZoneOffset.UTC).toInstant()));

        builder.addExtension(Extension.authorityKeyIdentifier, false,  new AuthorityKeyIdentifierStructure(ca));
        builder.addExtension(Extension.cRLNumber, false, new CRLNumber(BigInteger.valueOf(1)));


        for (X509Certificate certificate : revoked) {
            builder.addCRLEntry(certificate.getSerialNumber(), Date.from(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant()), CRLReason.keyCompromise);
        }

        JcaContentSignerBuilder contentSignerBuilder =
                new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm);

        contentSignerBuilder.setProvider("BC");

        X509CRLHolder crlHolder = builder.build(contentSignerBuilder.build(caPrivateKey));

        JcaX509CRLConverter converter = new JcaX509CRLConverter();

        converter.setProvider("BC");

        return converter.getCRL(crlHolder);
    }
}