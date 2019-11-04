package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

public class AuthorityKeyIdentifierObject implements ExtensionObject  {
    public final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = Extension.authorityKeyIdentifier;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }


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
            certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical,
                    overWrittenExtension);
            return;
        }


        if (certBuildContainer.getCaCert() == null) {
            throw new NullPointerException("Если есть authorityKeyIdentifier, то должен быть УЦ");
        }
        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical,
                new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(certBuildContainer.getCaCert()));
    }
    private Extension overWrittenExtension;
   public void setOverWritten(Extension overWrittenExtension){
       this.overWrittenExtension=overWrittenExtension;
        overWritten = true;
    }
    private boolean overWritten = false;
}
