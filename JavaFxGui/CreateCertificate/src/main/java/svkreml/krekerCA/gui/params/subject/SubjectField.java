package svkreml.krekerCA.gui.params.subject;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;


public class SubjectField {
    ASN1ObjectIdentifier asn1ObjectIdentifier;
    String name;
    TextField textField = new TextField();
    CheckBox isUsed = new CheckBox();

    public SubjectField(String name, ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this.asn1ObjectIdentifier = asn1ObjectIdentifier;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addFieldToGridPane(GridPane gridPane, int row) {
        gridPane.add(new Label(name + " (" + asn1ObjectIdentifier.getId() + ")"), 0, row);
        textField.setPrefColumnCount(40);
        gridPane.add(textField, 1, row);
        gridPane.add(isUsed, 11, row);
    }

    public String getTextField() {
        return textField.getText();
    }

    public boolean getIsUsed() {
        return isUsed.isSelected();
    }
}
