package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.*;

import java.io.IOException;

public class KeyUsageExtensionObject implements ExtensionObject {
    public  final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = Extension.keyUsage;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }
    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException {
        return null;
    }
    private Integer keyUsage;
    private boolean isCritical;
    public KeyUsageExtensionObject(boolean isCritical, Integer keyUsage) {
        this.keyUsage= keyUsage;
        this.isCritical=isCritical;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException {
        KeyUsage usage = new KeyUsage(keyUsage);

        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical, usage.getEncoded());
    }
}
