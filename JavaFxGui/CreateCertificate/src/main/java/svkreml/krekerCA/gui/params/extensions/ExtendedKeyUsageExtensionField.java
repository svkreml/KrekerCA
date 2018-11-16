package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.CertificatePoliciesExtensionObject;
import caJava.core.extensions.extParser.ExtendedKeyUsageExtensionObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class ExtendedKeyUsageExtensionField  implements ExtensionField {

    String name = "extendedKeyUsage";
    String discr = "2.5.29.37, улучшенный ключ";
    HashMap<CheckBox, String> keyUsages = new LinkedHashMap<>();

    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();

    public ExtendedKeyUsageExtensionField(){
        keyUsages.put(new CheckBox("TLS Web server authentication"), "1.3.6.1.5.5.7.3.1");
        keyUsages.put(new CheckBox("TLS Web client authentication"), "1.3.6.1.5.5.7.3.2");
        keyUsages.put(new CheckBox("Code signing"), "1.3.6.1.5.5.7.3.3");
        keyUsages.put(new CheckBox("E-mail protection"), "1.3.6.1.5.5.7.3.4");
        keyUsages.put(new CheckBox("Timestamping"), "1.3.6.1.5.5.7.3.8");
        keyUsages.put(new CheckBox("OCSPstamping"), "1.3.6.1.5.5.7.3.9");
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

    public int addFieldToGridPane(GridPane gridPane, int row) {
        gridPane.add(new Separator(), 0, ++row, 12, 1);
        gridPane.add(isCritical, 10, ++row);
        gridPane.add(isUsed, 11, row);
        gridPane.add(new Label(name), 0, row);
        gridPane.add(new Label(discr), 1, row, 4, 1);

        for (CheckBox checkBox : keyUsages.keySet()) {
            gridPane.add(checkBox, 1, ++row, 3, 1);
        }

        return row;
    }
}
