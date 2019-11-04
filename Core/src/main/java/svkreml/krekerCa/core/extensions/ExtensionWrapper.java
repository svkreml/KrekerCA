package svkreml.krekerCa.core.extensions;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;


import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;
import java.security.cert.X509Extension;

public class ExtensionWrapper {
    ASN1ObjectIdentifier oid;
    boolean isCritical;
    ASN1Encodable value;

    public ExtensionWrapper(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) {
        this.oid = oid;
        this.isCritical = isCritical;
        this.value = value;
    }

    public ASN1ObjectIdentifier getOid() {
        return oid;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public ASN1Encodable getValue() {
        return value;
    }

    public Extension getExtension() throws IOException {
        return new Extension(oid, isCritical, value.toASN1Primitive().getEncoded());//todo проверить
    }


}
