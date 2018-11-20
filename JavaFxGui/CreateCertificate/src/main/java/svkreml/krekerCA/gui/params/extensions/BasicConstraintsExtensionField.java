package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.BasicConstraintsExtensionObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class BasicConstraintsExtensionField extends BaseExtensionField {
    TextField cont = new TextField();

    public BasicConstraintsExtensionField() {
        super("basicConstraints", "2.5.29.19, Основные ограничения ");
    }

    public ExtensionObject getExtensionObject() {
        return new BasicConstraintsExtensionObject(isCritical.isSelected(), cont.getText());
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
        cont.setPrefColumnCount(40);
        propetyPane.add(new Label(name), 0, ++row);
        propetyPane.add(new Label(description), 1, row, 4, 1);
        propetyPane.add(new Label("(true, false или число)"), 1, ++row);
        propetyPane.add(cont, 2, row);
        return row;
    }
}
