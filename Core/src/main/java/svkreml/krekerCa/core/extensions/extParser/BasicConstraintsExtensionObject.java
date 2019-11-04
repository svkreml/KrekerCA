package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.*;

import java.io.IOException;

public class BasicConstraintsExtensionObject  implements ExtensionObject {
    public  final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = Extension.basicConstraints;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }



    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException {
        return null;
    }
    private String constraint;

    private boolean isCritical;
    public BasicConstraintsExtensionObject(boolean isCritical, String constraint) {

        this.isCritical=isCritical;
        this.constraint =constraint;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException {
        BasicConstraints constraints;
        if (constraint.contains("e")) // truE or falsE
            constraints = new BasicConstraints(Boolean.valueOf(constraint));
        else
            constraints = new BasicConstraints(Integer.parseInt(constraint));
        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical, constraints.getEncoded());
    }
}
