package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import svkreml.krekerCa.customOID.CustomExtension;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;

public class IssuerSignToolObject implements ExtensionObject {
    public  final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = CustomExtension.issuerSignTool;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }
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
            certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical, new DERUTF8String(texts[0]));
        } else if (texts.length > 1) {
            ASN1EncodableVector lines = new ASN1EncodableVector();
            for (String text : texts) {
                lines.add(new DERUTF8String(text));
            }
            certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical, new BERSequence(lines));
        }
    }
}
