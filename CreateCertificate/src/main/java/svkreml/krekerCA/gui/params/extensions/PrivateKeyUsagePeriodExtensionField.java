package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.PrivateKeyUsagePeriodObject;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.Date;

public class PrivateKeyUsagePeriodExtensionField extends BaseExtensionField {



    public void reset() {
        row = 0;
        propetyPane.getChildren().removeAll();
        addFieldToGridPane();
    }


    private final static String EXTENSION_IDENTIFIER_ID = PrivateKeyUsagePeriodObject.EXTENSION_IDENTIFIER.getId();
    DatePicker dateFrom = new DatePicker();
    DatePicker dateTo = new DatePicker();
    CheckBox auto = new CheckBox();
    public PrivateKeyUsagePeriodExtensionField() {
        super("PrivateKeyUsagePeriod", EXTENSION_IDENTIFIER_ID+", период использования закрытого ключа");
    }

    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }

    public ExtensionObject buildExtensionObject() {

        if (auto.isSelected() == false)
            return new PrivateKeyUsagePeriodObject(isCriticalCheckBox.isSelected(), Date.from(dateFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(dateTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        else
            return new PrivateKeyUsagePeriodObject(isCriticalCheckBox.isSelected());
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

    public int addFieldToGridPane( ) {

        Label label = new Label("Заполнить автоматически");
        Tooltip tooltip = new Tooltip("Дата заполнится автоматически из времени действия сертификата, поля при этом так и останутся пустыми и будут игнорироваться");
        label.setTooltip(tooltip);

        propetyPane.add(new Label("dateFrom"), 0, ++row);
        propetyPane.add(dateFrom, 1, row);
        propetyPane.add(new Label("dateTo"), 0, ++row);
        propetyPane.add(dateTo, 1, row);
        propetyPane.add(label, 0, ++row);
        propetyPane.add(auto, 1, row);
        return row;
    }
}
