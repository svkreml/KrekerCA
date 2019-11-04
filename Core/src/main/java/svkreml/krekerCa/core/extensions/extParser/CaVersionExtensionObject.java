package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import svkreml.krekerCa.customOID.CustomExtension;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;

public class CaVersionExtensionObject implements ExtensionObject {
    public  final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = CustomExtension.caVersion;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }

    private int number;
    private boolean isCritical;

    public CaVersionExtensionObject(boolean isCritical, int number) {
        this.number = number;
        this.isCritical = isCritical;
    }

    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException {
        return null;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException {
        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical, new ASN1Integer(number));
    }
}
