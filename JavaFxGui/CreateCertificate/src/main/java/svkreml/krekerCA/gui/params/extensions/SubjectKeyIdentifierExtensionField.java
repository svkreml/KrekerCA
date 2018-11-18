package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.ExtensionObject;
import caJava.core.extensions.extParser.SubjectKeyIdentifierExtensionObject;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class SubjectKeyIdentifierExtensionField extends BaseExtensionField {


    public SubjectKeyIdentifierExtensionField() {
        super("subjectKeyIdentifier", "2.5.29.14, Идентификатор ключа субъекта -- Открытый ключ субъекта");
    }

    public ExtensionObject getExtensionObject() {
        return new SubjectKeyIdentifierExtensionObject(isCritical.isSelected());
    }

    @Override
    public boolean getIsUsed() {
        return isUsed.isSelected();
    }

    @Override
    public Node getGui() {

        addFieldToGridPane(gridPane);
        return gridPane;
    }

    public int addFieldToGridPane(GridPane gridPane) {

        return row;
    }
}
