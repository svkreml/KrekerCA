package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public interface ExtensionField {

    public ExtensionObject getExtensionObject();

    boolean getIsUsed();

    public Node getGui();
}
