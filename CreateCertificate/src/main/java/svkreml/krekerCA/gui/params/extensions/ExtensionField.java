package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;

import java.io.IOException;
import java.security.cert.X509Certificate;

public interface ExtensionField {


    public String getOid();


    public ExtensionObject buildExtensionObject();

    boolean getIsUsedCheckBox();

    public void setIsUsedCheckBox(Boolean value);

    public Node getGui();


    public void reset();

    // public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert);
    public void setFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) throws IOException;

}
