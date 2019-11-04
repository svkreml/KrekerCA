package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.ExtendedKeyUsageExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class ExtendedKeyUsageExtensionField extends BaseExtensionField {


    public void reset(){
        row = 0;
        propetyPane.getChildren().removeAll();
        for (CheckBox checkBox : keyUsages.keySet()) {
            checkBox.setSelected(false);
        }
        addFieldToGridPane();
    }


    private final static String EXTENSION_IDENTIFIER_ID = ExtendedKeyUsageExtensionObject.EXTENSION_IDENTIFIER.getId();
    private HashMap<CheckBox, String> keyUsages = new LinkedHashMap<>();

    public ExtendedKeyUsageExtensionField() {
        super("extendedKeyUsage", "2.5.29.37, улучшенный ключ");
        keyUsages.put(new CheckBox("TLS Web server authentication"), "1.3.6.1.5.5.7.3.1");
        keyUsages.put(new CheckBox("TLS Web client authentication"), "1.3.6.1.5.5.7.3.2");
        keyUsages.put(new CheckBox("Code signing"), "1.3.6.1.5.5.7.3.3");
        keyUsages.put(new CheckBox("E-mail protection"), "1.3.6.1.5.5.7.3.4");
        keyUsages.put(new CheckBox("Timestamping"), "1.3.6.1.5.5.7.3.8");
        keyUsages.put(new CheckBox("OCSPstamping"), "1.3.6.1.5.5.7.3.9");
    }

    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }

    @Override
    public Node getGui() {
        addFieldToGridPane();
        return gridPane;
    }

    public ExtensionObject buildExtensionObject() {
        Vector<String> policiesArray = new Vector<>();
        for (CheckBox checkBox : keyUsages.keySet()) {
            if (checkBox.isSelected())
                policiesArray.add(keyUsages.get(checkBox));
        }

        return new ExtendedKeyUsageExtensionObject(isCriticalCheckBox.isSelected(), policiesArray.toArray(new String[]{}));
    }

    @Override
    public boolean getIsUsedCheckBox() {
        return isUsedCheckBox.isSelected();
    }

    public int addFieldToGridPane() {

        for (CheckBox checkBox : keyUsages.keySet()) {
            propetyPane.add(checkBox, 0, ++row, 3, 1);
        }

        return row;
    }
}
