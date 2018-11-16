package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;

public class CaVersionExtensionObject implements ExtensionObject {
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
        certBuildContainer.getX509v3CertificateBuilder().addExtension(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.21.1").intern(), isCritical, new ASN1Integer(number));
    }
}
