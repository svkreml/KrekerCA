package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import caJava.customOID.CustomExtension;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;

public class IssuerSignToolObject implements ExtensionObject {
    private String[] texts;
    private boolean isCritical;
    public IssuerSignToolObject(boolean isCritical, String[] lines) {
        this.texts = lines;
        this.isCritical = isCritical;
    }

    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException {
        return null;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException {

        if (texts.length == 1) {
            certBuildContainer.getX509v3CertificateBuilder().addExtension(CustomExtension.issuerSignTool, isCritical, new DERUTF8String(texts[0]));
        } else if (texts.length > 1) {
            ASN1EncodableVector lines = new ASN1EncodableVector();
            for (int i = 0; i < texts.length; i++) {
                lines.add(new DERUTF8String(texts[i]));
            }
            certBuildContainer.getX509v3CertificateBuilder().addExtension(CustomExtension.issuerSignTool, isCritical, new BERSequence(lines));
        }
    }
}
