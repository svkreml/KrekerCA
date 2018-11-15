package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.x509.*;

import java.io.IOException;

public class KeyUsageExtensionObject implements ExtensionObject {
    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException {
        return null;
    }
    Integer keyUsage;
    private boolean isCritical;
    public KeyUsageExtensionObject(boolean isCritical, Integer keyUsage) {
        this.keyUsage= keyUsage;
        this.isCritical=isCritical;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException {
        KeyUsage usage = new KeyUsage(keyUsage);

        certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.keyUsage, isCritical, usage.getEncoded());
    }
}
