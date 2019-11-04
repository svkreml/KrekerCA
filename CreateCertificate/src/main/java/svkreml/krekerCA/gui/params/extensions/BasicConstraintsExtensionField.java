package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.BasicConstraintsExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.security.cert.X509Certificate;

public class BasicConstraintsExtensionField extends BaseExtensionField {

    private final static String EXTENSION_IDENTIFIER_ID = BasicConstraintsExtensionObject.EXTENSION_IDENTIFIER.getId();
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }


    private TextField cont;

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }
    public void reset(){
        row = 0;
        propetyPane.getChildren().removeAll();
        addFieldToGridPane();
    }

    public BasicConstraintsExtensionField() {
        super("basicConstraints", EXTENSION_IDENTIFIER_ID+", Основные ограничения ");
    }

    public ExtensionObject buildExtensionObject() {
        return new BasicConstraintsExtensionObject(isCriticalCheckBox.isSelected(), cont.getText());
    }

    @Override
    public Node getGui() {
        addFieldToGridPane();
        return gridPane;
    }

    @Override
    public boolean getIsUsedCheckBox() {
        return isUsedCheckBox.isSelected();
    }

    public int addFieldToGridPane() {
        cont = new TextField();
        cont.setPrefColumnCount(40);
        propetyPane.add(new Label(name), 0, ++row);
        propetyPane.add(new Label(description), 1, row, 4, 1);
        propetyPane.add(new Label("(true, false или число)"), 1, ++row);
        propetyPane.add(cont, 2, row);
        return row;
    }
}
