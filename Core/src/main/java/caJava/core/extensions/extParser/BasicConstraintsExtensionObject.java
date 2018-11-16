package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.*;

import java.io.IOException;

public class BasicConstraintsExtensionObject  implements ExtensionObject {
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
        if (constraint.contains("e"))
            constraints = new BasicConstraints(Boolean.valueOf(constraint));
        else
            constraints = new BasicConstraints(Integer.parseInt(constraint));
        certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.basicConstraints, isCritical, constraints.getEncoded());
    }
}
