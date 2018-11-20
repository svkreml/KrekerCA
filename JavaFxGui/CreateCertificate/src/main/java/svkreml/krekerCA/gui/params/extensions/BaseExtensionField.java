package svkreml.krekerCA.gui.params.extensions;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

public abstract class BaseExtensionField implements ExtensionField {
    String name;
    String description;
    GridPane gridPane = new GridPane();
    GridPane propetyPane = new GridPane();
    CheckBox isUsed;
    CheckBox isCritical = new CheckBox("Критическое расширение");
    int row = 0;
    int propetyPaneRow = 0;

    public BaseExtensionField(String name, String discr) {
        this.name = name;
        this.description = discr;

        isUsed = new CheckBox("Расширение: " + name);
        isUsed.setOnAction(this::changeIsUsed);

        Separator separator = new Separator();
        separator.setPadding(new Insets(15,0,5,0 ));

        gridPane.add(separator, 0, ++row, 20, 1);
        gridPane.add(isUsed, 0, ++row, 19, 1);
        //gridPane.add(propetyPane, 0, ++row);
        propetyPaneRow = ++row;

        propetyPane.add(new Label("Описание: " + discr), 0, 0, 19, 1);
        propetyPane.add(isCritical, 0, ++row);

       // row = row + 3;
    }

    private void changeIsUsed(ActionEvent actionEvent) {
        if (getIsUsed()) {
            gridPane.add(propetyPane, 0, propetyPaneRow);
        } else {
            gridPane.getChildren().remove(propetyPane);
        }
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
