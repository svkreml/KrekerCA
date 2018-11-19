package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class AuthorityKeyIdentifierObject implements ExtensionObject  {
    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws Exception {
        return null;
    }

    public AuthorityKeyIdentifierObject(boolean isCritical) {
        this.isCritical = isCritical;
    }

    private boolean isCritical;
    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws Exception {
        if(overWritten){
            certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.authorityKeyIdentifier, isCritical,
                    overWrittenExtension);
            return;
        }


        if (certBuildContainer.getCaCert() == null) {
            throw new NullPointerException("Если есть authorityKeyIdentifier, то должен быть УЦ");
        }
        GeneralName generalName = new GeneralName(new X500Name(certBuildContainer.getCaCert().getSubjectX500Principal().getName(X500Principal.RFC2253)));
        GeneralNames generalNames = new GeneralNames(generalName);
        X509CertificateHolder certificateHolder = new X509CertificateHolder(certBuildContainer.getCaCert().getEncoded());

        certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.authorityKeyIdentifier, isCritical,
                new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(certBuildContainer.getCaCert()));


        /*
        *
        certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.authorityKeyIdentifier, isCritical, certificateHolder.getSubjectPublicKeyInfo());
        certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.authorityKeyIdentifier, isCritical, Base64.decode("MIIBTIAU9HR58oM4uhKFCvktauXuSfiK29ihggEgpIIBHDCCARgxJjAkBgkqhkiG9w0BCQEWF2ouc2hpcnZhbnlhbkB2b3NraG9kLnJ1MQswCQYDVQQGEwJSVTEcMBoGA1UECAwTNzcg0LMuINCc0L7RgdC60LLQsDEVMBMGA1UEBwwM0JzQvtGB0LrQstCwMR8wHQYDVQQJDBbQo9C00LDQu9GM0YbQvtCy0LAsIDg1MRIwEAYDVQQKDAnQndCY0JQtMTIxGDAWBgUqhQNkARINMTA0NzcwMjAyNjcwMTEaMBgGCCqFAwOBAwEBEgwwMDc3MTA0NzQzNzUxQTA/BgNVBAMMONCT0L7Qu9C+0LLQvdC+0Lkg0KPQpiDQndCY0JQtMTIg0JPQntCh0KIgMjAxMiAo0YLQtdGB0YIpghAQZWn7UYixlOgR9YWTSUzX"));*/


    }
    Extension overWrittenExtension;
   public void setOverWritten(Extension overWrittenExtension){
       this.overWrittenExtension=overWrittenExtension;
        overWritten = true;
    }
    boolean overWritten = false;
}
