package svkreml.krekerCa.core.extensions.extParser;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import svkreml.krekerCa.core.extensions.CertBuildContainer;

import java.security.NoSuchAlgorithmException;

public class SubjectKeyIdentifierExtensionObject implements ExtensionObject {
    final public static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = Extension.subjectKeyIdentifier;
    final public static String EXTENSION_IDENTIFIER_OID = Extension.subjectKeyIdentifier.getId();
    private boolean isCritical = false;

    public SubjectKeyIdentifierExtensionObject(boolean isCritical) {
        this.isCritical = isCritical;
    }

    public SubjectKeyIdentifierExtensionObject() {
    }

    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }

    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer)  {
        return null;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws NoSuchAlgorithmException, CertIOException {

        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER,
                isCritical, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(certBuildContainer.getPublicKey()));
    }
}
