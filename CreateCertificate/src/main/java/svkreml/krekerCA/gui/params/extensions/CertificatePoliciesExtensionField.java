package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.CertificatePoliciesExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class CertificatePoliciesExtensionField extends BaseExtensionField {


    public void reset() {
        row = 0;
        propetyPane.getChildren().removeAll();
        addFieldToGridPane();
        for (CheckBox checkBox : policies.keySet()) {
            checkBox.setSelected(false);
        }
    }


    private final static String EXTENSION_IDENTIFIER_ID = CertificatePoliciesExtensionObject.EXTENSION_IDENTIFIER.getId();
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
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {
        gridPane.getChildren().remove(propetyPane);
        gridPane.add(propetyPane, 0, propetyPaneRow);
    }

    @Override
    public Node getGui() {

        addFieldToGridPane();
        return gridPane;
    }

    public ExtensionObject buildExtensionObject() {
        Vector<String> policiesArray = new Vector<>();
        for (CheckBox checkBox : policies.keySet()) {
            if (checkBox.isSelected())
                policiesArray.add(policies.get(checkBox));
        }

        return new CertificatePoliciesExtensionObject(isCriticalCheckBox.isSelected(), policiesArray.toArray(new String[]{}));
    }

    @Override
    public boolean getIsUsedCheckBox() {
        return isUsedCheckBox.isSelected();
    }

    public int addFieldToGridPane() {
        for (CheckBox checkBox : policies.keySet()) {
            propetyPane.add(checkBox, 0, ++row, 3, 1);
        }
        return row;
    }
}
