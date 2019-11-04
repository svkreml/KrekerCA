package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.*;

import java.io.IOException;
import java.security.cert.X509Certificate;

public class AuthorityInfoAccessExtensionObject implements ExtensionObject {
  public  final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = Extension.authorityInfoAccess;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }

    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException {
        return null;
    }
    private String[] url;
    private String[] ocsps;
    private boolean isCritical;
    public AuthorityInfoAccessExtensionObject(boolean isCritical, String[] urls, String[] ocsps) {
        this.url = urls;
        this.ocsps = ocsps;
        this.isCritical=isCritical;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException {
        ASN1EncodableVector vec = new ASN1EncodableVector();

        for (String ocsp : ocsps) {
            vec.add(new AccessDescription(AccessDescription.id_ad_ocsp, new GeneralName(GeneralName.uniformResourceIdentifier, ocsp)));
        }
        for (String s : url) {
            vec.add(new AccessDescription(AccessDescription.id_ad_caIssuers, new GeneralName(GeneralName.uniformResourceIdentifier, s)));
        }

        AuthorityInformationAccess authorityInformationAccess = AuthorityInformationAccess.getInstance(new DERSequence(vec));
        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical, authorityInformationAccess);
    }
}
