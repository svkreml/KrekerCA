package svkreml.krekerCA.gui.params.subject;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class SubjectField {
    String name;
    TextField textField = new TextField();
    CheckBox isUsed = new CheckBox();

    public SubjectField(String name) {
        this.name = name;
    }
    public void addFieldToGridPane(GridPane gridPane, int row){
        gridPane.add(new Label(name),0,row);
        gridPane.add(textField,1,row);
        gridPane.add(isUsed,11,row);
    }

    public String getTextField() {
        return textField.getText();
    }

    public boolean getIsUsed() {
        return isUsed.isSelected();
    }
}
