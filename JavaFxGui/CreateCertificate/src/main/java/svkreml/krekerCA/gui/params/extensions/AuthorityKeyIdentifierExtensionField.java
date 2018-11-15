package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.AuthorityKeyIdentifierObject;
import caJava.core.extensions.extParser.CRLDistributionPointsObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Vector;

public class AuthorityKeyIdentifierExtensionField implements ExtensionField  {
    String name = "authorityKeyIdentifier";
    String discr = "2.5.29.35, данные о вышестоящем сертификате";



    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();

    public ExtensionObject getExtensionObject() {
        return new AuthorityKeyIdentifierObject(isCritical.isSelected());
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
