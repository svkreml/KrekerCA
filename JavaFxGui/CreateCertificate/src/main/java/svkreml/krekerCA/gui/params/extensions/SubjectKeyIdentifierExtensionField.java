package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.AuthorityKeyIdentifierObject;
import caJava.core.extensions.extParser.ExtensionObject;
import caJava.core.extensions.extParser.SubjectKeyIdentifierExtensionObject;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

public class SubjectKeyIdentifierExtensionField implements ExtensionField  {
    String name = "subjectKeyIdentifier";
    String discr = "2.5.29.14, Идентификатор ключа субъекта -- Открытый ключ субъекта";



    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();

    public ExtensionObject getExtensionObject() {
        return new SubjectKeyIdentifierExtensionObject(isCritical.isSelected());
    }

    @Override
    public boolean getIsUsed() {
        return isUsed.isSelected();
    }

    public int addFieldToGridPane(GridPane gridPane, int row) {
        gridPane.add( new Separator(), 0, ++row,12,1);
        gridPane.add(isCritical, 10, ++row);
        gridPane.add(isUsed, 11, row);
        gridPane.add(new Label(name), 0, row);
        gridPane.add(new Label(discr), 1, row, 4, 1);
        return row;
    }
}
