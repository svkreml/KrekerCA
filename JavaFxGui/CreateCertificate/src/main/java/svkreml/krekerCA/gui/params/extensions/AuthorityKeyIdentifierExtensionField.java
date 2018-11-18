package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.AuthorityKeyIdentifierObject;
import caJava.core.extensions.extParser.CRLDistributionPointsObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Vector;

public class AuthorityKeyIdentifierExtensionField extends BaseExtensionField  {
    public AuthorityKeyIdentifierExtensionField() {
        super( "authorityKeyIdentifier", "2.5.29.35, идентификатор ключа центра сертификатов");
    }

    @Override
    public Node getGui() {

        //addFieldToGridPane(gridPane, 3);
        return gridPane;
    }



    public ExtensionObject getExtensionObject() {
        return new AuthorityKeyIdentifierObject(isCritical.isSelected());
    }



}
