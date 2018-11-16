package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.CertificatePoliciesExtensionObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class CertificatePoliciesExtensionField implements ExtensionField {

    String name = "certificatePolicies";
    String discr = "2.5.29.32, Политики сертификата";
    HashMap<CheckBox, String> policies = new LinkedHashMap<>();

    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();

    public CertificatePoliciesExtensionField() {
        policies.put(new CheckBox("класс средства ЭП КС1"), "1.2.643.100.113.1");
        policies.put(new CheckBox("класс средства ЭП КС2"), "1.2.643.100.113.2");
        policies.put(new CheckBox("класс средства ЭП КС3"), "1.2.643.100.113.3");
        policies.put(new CheckBox("класс средства ЭП КВ1"), "1.2.643.100.113.4");
        policies.put(new CheckBox("класс средства ЭП КВ2"), "1.2.643.100.113.5");
        policies.put(new CheckBox("класс средства ЭП КА1"), "1.2.643.100.113.6");
    }

    public ExtensionObject getExtensionObject() {
        Vector<String> policiesArray = new Vector<>();
        for (CheckBox checkBox : policies.keySet()) {
            if (checkBox.isSelected())
                policiesArray.add(policies.get(checkBox));
        }

        return new CertificatePoliciesExtensionObject(isCritical.isSelected(), policiesArray.toArray(new String[]{}));
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

        for (CheckBox checkBox : policies.keySet()) {
            gridPane.add(checkBox, 1, ++row, 3, 1);
        }

        return row;
    }
}
