package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import javax.security.auth.x500.X500Principal;

public class SubjectKeyIdentifierExtensionObject implements ExtensionObject  {
    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws Exception {
        return null;
    }

    public SubjectKeyIdentifierExtensionObject(boolean isCritical) {
        this.isCritical = isCritical;
    }

    private boolean isCritical;
    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws Exception {

        certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.subjectKeyIdentifier,
                isCritical, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(certBuildContainer.getPublicKey()));
    }
}
