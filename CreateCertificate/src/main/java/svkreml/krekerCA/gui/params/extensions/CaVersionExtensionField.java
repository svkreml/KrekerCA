package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.CaVersionExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.security.cert.X509Certificate;
import java.util.Vector;


public class CaVersionExtensionField extends BaseExtensionField {
    private final static String EXTENSION_IDENTIFIER_ID = CaVersionExtensionObject.EXTENSION_IDENTIFIER.getId();
    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();
    private Vector<TextField> texts;
    private Vector<Label> textslabel;
    private int rowOfText;

    public CaVersionExtensionField() {
        super("сaVersion", "1.3.6.1.4.1.311.21.1, Версия ЦС, (только одна строка, число)");
    }

    public void reset() {
        row = 0;
        propetyPane.getChildren().removeAll();
        addFieldToGridPane();
    }


    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    public ExtensionObject buildExtensionObject() {
        String[] lines = new String[texts.size()];
        for (int i = 0; i < texts.size(); i++) {
            lines[i] = texts.elementAt(i).getText();
        }
        return new CaVersionExtensionObject(isCritical.isSelected(), Integer.parseInt(lines[0]));
    }

    @Override
    public boolean getIsUsedCheckBox() {
        return isUsed.isSelected();
    }


    @Override
    public Node getGui() {
        addFieldToGridPane();
        return gridPane;
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }

    public int addFieldToGridPane() {
        texts = new Vector<>();
        textslabel = new Vector<>();
        gridPane.add(new Separator(), 0, ++row, 12, 1);
        gridPane.add(isCritical, 10, ++row);
        gridPane.add(isUsed, 11, row);
        gridPane.add(new Label(name), 0, row);
        gridPane.add(new Label(description), 1, row, 4, 1);
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
        textslabel.remove(textslabel.lastElement());
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
