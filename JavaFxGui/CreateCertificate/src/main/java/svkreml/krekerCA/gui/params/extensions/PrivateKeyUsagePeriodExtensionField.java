package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.ExtensionObject;
import caJava.core.extensions.extParser.PrivateKeyUsagePeriodObject;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.ZoneId;
import java.util.Date;

public class PrivateKeyUsagePeriodExtensionField implements ExtensionField {
    String name = "PrivateKeyUsagePeriod";
    String discr = "2.5.29.16, период использования закрытого ключа";


    DatePicker dateFrom = new DatePicker();
    DatePicker dateTo = new DatePicker();

    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();
    CheckBox auto = new CheckBox();

    public ExtensionObject getExtensionObject() {

        if (auto.isSelected()==false)
            return new PrivateKeyUsagePeriodObject(isCritical.isSelected(), Date.from(dateFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(dateTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        else
            return new PrivateKeyUsagePeriodObject(isCritical.isSelected());
    }

    @Override
    public boolean getIsUsed() {
        return isUsed.isSelected();
    }

    public int addFieldToGridPane(GridPane gridPane, int row) {
        gridPane.add( new Separator(), 0, ++row,12,1);
        gridPane.add(isCritical, 10,++row);
        gridPane.add(isUsed, 11, row);
        gridPane.add(new Label(name), 0, row);
        gridPane.add(new Label(discr), 1, row, 4, 1);
        gridPane.add(new Label("dateFrom"), 1, ++row);
        gridPane.add(dateFrom, 2, row);
        gridPane.add(new Label("dateTo"), 1, ++row);
        gridPane.add(dateTo, 2, row);
        Label label = new Label("Заполнить автоматически");
        Tooltip tooltip = new Tooltip("Дата заполнится автоматически из времени действия сертификата, поля при этом так и останутся пустыми и будут игнорироваться");
        label.setTooltip(tooltip);
        gridPane.add(label, 1, ++row);
        gridPane.add(auto, 2, row);
        return row;
    }
}
