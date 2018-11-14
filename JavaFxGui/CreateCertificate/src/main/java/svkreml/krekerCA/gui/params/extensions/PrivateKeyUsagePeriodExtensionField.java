package svkreml.krekerCA.gui.params.extensions;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class PrivateKeyUsagePeriodExtensionField implements ExtensionField {
    String name = "PrivateKeyUsagePeriod";
    TextField dateFrom = new TextField();
    TextField dateTo = new TextField();

    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();
    CheckBox auto = new CheckBox();

    public int addFieldToGridPane(GridPane gridPane, int row) {
        gridPane.add(isCritical, 10, row);
        gridPane.add(isUsed, 11, row);
        gridPane.add(new Label(name), 0, row);
        gridPane.add(new Label("dateFrom"), 1, ++row);
        gridPane.add(dateFrom, 2, row);
        gridPane.add(new Label("dateTo"), 1, ++row);
        gridPane.add(dateTo, 2, row);
        gridPane.add(new Label("Заполнить автоматически"), 1, ++row);
        gridPane.add(auto, 2, row);
        return row;
    }
}
