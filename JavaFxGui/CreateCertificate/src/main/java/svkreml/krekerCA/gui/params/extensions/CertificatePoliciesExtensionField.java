package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.CertificatePoliciesExtensionObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class CertificatePoliciesExtensionField extends BaseExtensionField {


    HashMap<CheckBox, String> policies = new LinkedHashMap<>();


    public CertificatePoliciesExtensionField() {
        super("certificatePolicies", "2.5.29.32, Политики сертификата");
        policies.put(new CheckBox("Все политики выдачи"), "2.5.29.32.0");
        policies.put(new CheckBox("класс средства ЭП КС1"), "1.2.643.100.113.1");
        policies.put(new CheckBox("класс средства ЭП КС2"), "1.2.643.100.113.2");
        policies.put(new CheckBox("класс средства ЭП КС3"), "1.2.643.100.113.3");
        policies.put(new CheckBox("класс средства ЭП КВ1"), "1.2.643.100.113.4");
        policies.put(new CheckBox("класс средства ЭП КВ2"), "1.2.643.100.113.5");
        policies.put(new CheckBox("класс средства ЭП КА1"), "1.2.643.100.113.6");
    }

    @Override
    public Node getGui() {

        addFieldToGridPane(gridPane);
        return gridPane;
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

    public int addFieldToGridPane(GridPane gridPane) {
        for (CheckBox checkBox : policies.keySet()) {
            propetyPane.add(checkBox, 0, ++row, 3, 1);
        }

        return row;
    }
}