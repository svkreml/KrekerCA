package svkreml.krekerCA.gui.params.extensions;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

public abstract class BaseExtensionField implements ExtensionField {
    String name;
    String description;
    GridPane gridPane = new GridPane();
    CheckBox isUsed = new CheckBox("Использовать при генерации");
    CheckBox isCritical = new CheckBox("Критическое расширение");
    int row = 0;

    public BaseExtensionField(String name, String discr) {
        this.name = name;
        this.description = discr;
        gridPane.add(new Separator(), 0, ++row,20,1);
        gridPane.add(new Label("Имя: " + name), 0, ++row,19,1);
        gridPane.add(new Label("                                                                                                                                                                                                        "), 0, row,19,1);
        gridPane.add(isUsed, 20, row);
        gridPane.add(new Label("Описание: " + discr), 0, ++row,19,1);
        gridPane.add(isCritical, 19, row);
        row=row+3;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean getIsUsed() {
        return isUsed.isSelected();
    }
}
