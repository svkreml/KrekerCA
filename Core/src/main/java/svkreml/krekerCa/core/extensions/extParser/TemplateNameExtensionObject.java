package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import svkreml.krekerCa.customOID.CustomExtension;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;

public class TemplateNameExtensionObject implements ExtensionObject {
   public final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = CustomExtension.subjectSignTool;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }

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
            // FIXME требуется описание
            certBuildContainer.getX509v3CertificateBuilder().addExtension(CustomExtension.templateName, isCritical, new DERUTF8String(lines[0]));
        } else if (lines.length > 1) {
            ASN1EncodableVector vector = new ASN1EncodableVector();
            for (String line : lines) {
                vector.add(new DERUTF8String(line));
            }
            certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical, new BERSequence(vector));
        }
    }
}
