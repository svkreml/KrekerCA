package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import caJava.customOID.CustomExtension;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;

public class TemplateNameExtensionObject implements ExtensionObject {
    private String[] lines;
    private boolean isCritical;

    public TemplateNameExtensionObject(boolean isCritical, String[] lines) {
        this.lines = lines;
        this.isCritical = isCritical;
    }

    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException {
        return null;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException {
        if (lines.length == 1) {
            certBuildContainer.getX509v3CertificateBuilder().addExtension(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.20.2").intern(), isCritical, new DERUTF8String(lines[0]));
        } else if (lines.length > 1) {
            ASN1EncodableVector vector = new ASN1EncodableVector();
            for (int i = 0; i < lines.length; i++) {
                vector.add(new DERUTF8String(lines[i]));
            }
            certBuildContainer.getX509v3CertificateBuilder().addExtension(CustomExtension.subjectSignTool, isCritical, new BERSequence(vector));
        }
    }
}
