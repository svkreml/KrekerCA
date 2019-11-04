package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.*;

import java.io.IOException;

public class CRLDistributionPointsObject implements ExtensionObject {
    public  final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = Extension.cRLDistributionPoints;
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }
    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException {
        return null;
    }
    private String[] crls;
    private boolean isCritical;
    public CRLDistributionPointsObject(boolean isCritical, String[] crls) {
        this.crls= crls;
        this.isCritical=isCritical;
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException {
        DistributionPoint[] distPoints = new DistributionPoint[crls.length];
        for (int i = 0; i < crls.length; i++) {
            DistributionPointName distributionPoint = new DistributionPointName(new GeneralNames(new GeneralName(GeneralName.uniformResourceIdentifier, crls[i])));
            distPoints[i] = new DistributionPoint(distributionPoint, null, null);
        }
        System.out.println("добавление списка отзывов " + EXTENSION_IDENTIFIER);
        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, isCritical, new CRLDistPoint(distPoints));
    }
}
