package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtendedKeyUsageExtensionObject implements ExtensionObject {
    public final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = Extension.extendedKeyUsage;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }

    boolean isCritical;
    private String[] usages;

    //@Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException, Exception {
        return null;
    }

    public ExtendedKeyUsageExtensionObject(boolean isCritical, String[] usages) {
        this.isCritical = isCritical;
        this.usages = usages;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException, Exception {
        List<ASN1ObjectIdentifier> list = new ArrayList<>();

        for (int i = 0; i < usages.length; i++) {
            list.add(new ASN1ObjectIdentifier(usages[i]));
        }
        KeyPurposeId[] kps = new KeyPurposeId[list.size()];
        int idx = 0;
        for (ASN1ObjectIdentifier oid : list) {
            kps[idx++] = KeyPurposeId.getInstance(oid);
        }
        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER,
                isCritical, new ExtendedKeyUsage(kps));
    }
}
