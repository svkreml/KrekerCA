package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.BasicConstraintsExtensionObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class BasicConstraintsExtensionField implements ExtensionField {
    String name = "basicConstraints";
    String discr = "2.5.29.19, Основные ограничения ";
    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();
    TextField cont = new TextField();

    public ExtensionObject getExtensionObject() {
        return new BasicConstraintsExtensionObject(isCritical.isSelected(), cont.getText());
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
        gridPane.add(new Label(discr), 1, row, 4, 1);

        gridPane.add(new Label("(true, false или число)"), 1, ++row);
        gridPane.add(cont, 2, row);
        return row;
    }
}
