package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.ExtensionObject;
import caJava.core.extensions.extParser.IssuerSignToolObject;
import caJava.core.extensions.extParser.SubjectSignToolObject;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Vector;

public class IssuerSignToolExtensionField implements ExtensionField {
    String name = "issueSignTool";
    String discr = "1.2.643.100.111, Средство электронной подписи издателя (4 строки)";

    Vector<TextField> texts = new Vector<>();
    Vector<Label> textslabel = new Vector<>();
    int rowOfText;


    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();

    public ExtensionObject getExtensionObject() {
        String[] urlsArray = new String[texts.size()];
        for (int i = 0; i < texts.size(); i++) {
            urlsArray[i] = texts.elementAt(i).getText();
        }
        return new IssuerSignToolObject(isCritical.isSelected(), urlsArray);
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
        row = addTextLine(gridPane, row);

        rowOfText = row;
        row = row + 20;
        Button add = new Button("ещё");
        gridPane.add(add, 0, ++row);
        add.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                rowOfText = addTextLine(gridPane, rowOfText);
        });
        Button remove = new Button("удалить");
        gridPane.add(remove, 0, ++row);
        remove.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            rowOfText = removeTextLine(gridPane, rowOfText);
        });
        return row;
    }
    private int removeTextLine(GridPane gridPane, int row) {
        gridPane.getChildren().remove(texts.lastElement());
        gridPane.getChildren().remove(textslabel.lastElement());
        texts.remove(texts.lastElement());
        textslabel.remove( textslabel.lastElement());
        return row--;
    }
    private int addTextLine(GridPane gridPane, int row) {
        TextField url = new TextField();
        Label label = new Label("Строка текста");
        textslabel.add(label);
        gridPane.add(label, 1, ++row);
        gridPane.add(url, 2, row);
        texts.add(url);
        return row;
    }
}
