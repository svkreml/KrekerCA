package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.ExtensionObject;
import caJava.core.extensions.extParser.PrivateKeyUsagePeriodObject;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

import java.time.ZoneId;
import java.util.Date;

public class PrivateKeyUsagePeriodExtensionField extends BaseExtensionField {


    DatePicker dateFrom = new DatePicker();
    DatePicker dateTo = new DatePicker();


    CheckBox auto = new CheckBox();

    public ExtensionObject getExtensionObject() {

        if (auto.isSelected() == false)
            return new PrivateKeyUsagePeriodObject(isCritical.isSelected(), Date.from(dateFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(dateTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        else
            return new PrivateKeyUsagePeriodObject(isCritical.isSelected());
    }

    public PrivateKeyUsagePeriodExtensionField() {
        super("PrivateKeyUsagePeriod", "2.5.29.16, период использования закрытого ключа");
    }

    @Override
    public Node getGui() {

        addFieldToGridPane(gridPane);
        return gridPane;
    }

    @Override
    public boolean getIsUsed() {
        return isUsed.isSelected();
    }

    public int addFieldToGridPane(GridPane gridPane) {

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
