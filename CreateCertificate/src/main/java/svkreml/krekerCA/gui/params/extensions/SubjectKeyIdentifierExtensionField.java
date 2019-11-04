package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.SubjectKeyIdentifierExtensionObject;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.security.cert.X509Certificate;

public class SubjectKeyIdentifierExtensionField extends BaseExtensionField {

    public void reset() {
        row = 0;
        propetyPane.getChildren().removeAll();
        addFieldToGridPane();
    }


    private final static String EXTENSION_IDENTIFIER_ID = SubjectKeyIdentifierExtensionObject.EXTENSION_IDENTIFIER.getId();

    public SubjectKeyIdentifierExtensionField() {
        super("subjectKeyIdentifier", "2.5.29.14, Идентификатор ключа субъекта -- Открытый ключ субъекта");
    }

    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }

    public ExtensionObject buildExtensionObject() {
        return new SubjectKeyIdentifierExtensionObject(isCriticalCheckBox.isSelected());
    }

    @Override
    public boolean getIsUsedCheckBox() {
        return isUsedCheckBox.isSelected();
    }

    @Override
    public Node getGui() {

        addFieldToGridPane();
        return gridPane;
    }

    public int addFieldToGridPane( ) {
        return row;
    }
}
