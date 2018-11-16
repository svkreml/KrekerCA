package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.*;

import java.io.IOException;

public class AuthorityInfoAccessExtensionObject implements ExtensionObject {
    @Override
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

        for (int i = 0; i < ocsps.length; i++) {
            vec.add(new AccessDescription(AccessDescription.id_ad_ocsp, new GeneralName(GeneralName.uniformResourceIdentifier, ocsps[i])));
        }
        for (int i = 0; i < url.length; i++) {
            vec.add(new AccessDescription(AccessDescription.id_ad_caIssuers, new GeneralName(GeneralName.uniformResourceIdentifier, url[i])));
        }

        AuthorityInformationAccess authorityInformationAccess = AuthorityInformationAccess.getInstance(new DERSequence(vec));
        certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.authorityInfoAccess, isCritical, authorityInformationAccess);
    }
}
