package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public interface ExtensionField {
    public int addFieldToGridPane(GridPane gridPane, int row);
    public ExtensionObject getExtensionObject();

    boolean getIsUsed();
}
