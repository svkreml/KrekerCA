package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.PolicyInformation;

import java.io.IOException;

public class CertificatePoliciesExtensionObject implements ExtensionObject  {
    public  final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = Extension.certificatePolicies;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }

    private boolean isCritical;
    private String[] polices;
    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException, Exception {
        return null;
    }

    public CertificatePoliciesExtensionObject(boolean isCritical, String[] polices) {
        this.isCritical = isCritical;
        this.polices = polices;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException, Exception {
        ASN1EncodableVector policyExtensions = new ASN1EncodableVector();
        for (String police : polices) {
            policyExtensions.add(new PolicyInformation(new ASN1ObjectIdentifier(police)));
        }
        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical, new DERSequence(policyExtensions));
    }
}
