package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.ExtensionObject;
import caJava.core.extensions.extParser.IssuerSignToolObject;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Vector;

public class IssuerSignToolExtensionField extends BaseExtensionField {
    Vector<TextField> texts = new Vector<>();
    Vector<Label> textslabel = new Vector<>();
    int rowOfText;

    public IssuerSignToolExtensionField() {
        super("issueSignTool", "1.2.643.100.111, Средство электронной подписи издателя (4 строки)");
    }

    public ExtensionObject getExtensionObject() {
        String[] urlsArray = new String[texts.size()];
        for (int i = 0; i < texts.size(); i++) {
            urlsArray[i] = texts.elementAt(i).getText();
        }
        return new IssuerSignToolObject(isCritical.isSelected(), urlsArray);
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

        row = addTextLine(gridPane, row);
        row = addTextLine(gridPane, row);
        row = addTextLine(gridPane, row);
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
            if(!texts.isEmpty())
            rowOfText = removeTextLine(gridPane, rowOfText);
        });
        return row;
    }

    private int removeTextLine(GridPane gridPane, int row) {
        gridPane.getChildren().remove(texts.lastElement());
        gridPane.getChildren().remove(textslabel.lastElement());
        texts.remove(texts.lastElement());
        textslabel.remove(textslabel.lastElement());
        return row--;
    }

    private int addTextLine(GridPane gridPane, int row) {
        TextField url = new TextField();
        url.setPrefColumnCount(40);
        Label label = new Label("Строка текста");
        textslabel.add(label);
        gridPane.add(label, 0, ++row);
        gridPane.add(url, 1, row);
        texts.add(url);
        return row;
    }
}
