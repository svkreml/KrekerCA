package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;

import java.io.IOException;

public class CertificatePoliciesExtensionObject implements ExtensionObject  {
    boolean isCritical;
    String[] polices;
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
        for (int i = 0; i < polices.length; i++) {
            policyExtensions.add(new PolicyInformation(new ASN1ObjectIdentifier(polices[i])));
        }
        certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.certificatePolicies, isCritical, new DERSequence(policyExtensions));
    }
}
