package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;
import java.security.cert.X509Certificate;

public interface ExtensionObject {
    public String getOid();
    //public void copyFromCert(X509Certificate donorCert);
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException, Exception;
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException, Exception;


/*
    public String getName();
    public String getOid();
    public String getDescription();
    public String getParametersDescription();*/
}
