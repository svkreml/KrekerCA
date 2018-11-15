package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import org.bouncycastle.asn1.x509.*;

import java.io.IOException;

public class CRLDistributionPointsObject implements ExtensionObject {
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
        System.out.println("добавление списка отзывов " + Extension.cRLDistributionPoints);
        certBuildContainer.getX509v3CertificateBuilder().addExtension(Extension.cRLDistributionPoints, isCritical, new CRLDistPoint(distPoints));
    }
}
