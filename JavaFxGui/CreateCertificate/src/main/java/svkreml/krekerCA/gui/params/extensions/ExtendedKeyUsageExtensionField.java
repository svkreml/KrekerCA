package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.ExtendedKeyUsageExtensionObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class ExtendedKeyUsageExtensionField extends BaseExtensionField {


    HashMap<CheckBox, String> keyUsages = new LinkedHashMap<>();


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
    public Node getGui() {
        addFieldToGridPane(gridPane);
        return gridPane;
    }

    public ExtensionObject getExtensionObject() {
        Vector<String> policiesArray = new Vector<>();
        for (CheckBox checkBox : keyUsages.keySet()) {
            if (checkBox.isSelected())
                policiesArray.add(keyUsages.get(checkBox));
        }

        return new ExtendedKeyUsageExtensionObject(isCritical.isSelected(), policiesArray.toArray(new String[]{}));
    }

    @Override
    public boolean getIsUsed() {
        return isUsed.isSelected();
    }

    public int addFieldToGridPane(GridPane gridPane) {

        for (CheckBox checkBox : keyUsages.keySet()) {
            gridPane.add(checkBox, 1, ++row, 3, 1);
        }

        return row;
    }
}
