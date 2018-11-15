package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;

public interface ExtensionObject {

    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException, Exception;
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException, Exception;
}
