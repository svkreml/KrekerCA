package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.SubjectSignToolObject;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.security.cert.X509Certificate;
import java.util.Vector;

public class SubjectSignToolExtensionField extends BaseExtensionField {

    public void reset() {
        row = 0;
        propetyPane.getChildren().removeAll();
        addFieldToGridPane();
    }

    private final static String EXTENSION_IDENTIFIER_ID = SubjectSignToolObject.EXTENSION_IDENTIFIER.getId();
    Vector<TextField> texts = new Vector<>();
    Vector<Label> textslabel = new Vector<>();
    int rowOfText;
    public SubjectSignToolExtensionField() {
        super("subjectSignTool", EXTENSION_IDENTIFIER_ID+", Средство электронной подписи владельца (1 строка)");
    }

    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    public ExtensionObject buildExtensionObject() {
        String[] urlsArray = new String[texts.size()];
        for (int i = 0; i < texts.size(); i++) {
            urlsArray[i] = texts.elementAt(i).getText();
        }
        return new SubjectSignToolObject(isCriticalCheckBox.isSelected(), urlsArray);
    }

    @Override
    public boolean getIsUsedCheckBox() {
        return isUsedCheckBox.isSelected();
    }

    @Override
    public Node getGui() {

        addFieldToGridPane();
        return gridPane;
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }

    public int addFieldToGridPane( ) {

        row = addTextLine(gridPane, row);

        rowOfText = row;
        row = row + 20;
        Button add = new Button("ещё");
        propetyPane.add(add, 0, ++row);
        add.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            rowOfText = addTextLine(gridPane, rowOfText);
        });
        Button remove = new Button("удалить");
        propetyPane.add(remove, 0, ++row);
        remove.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (!texts.isEmpty())
                rowOfText = removeTextLine(gridPane, rowOfText);
        });
        return row;
    }

    private int removeTextLine(GridPane gridPane, int row) {
        propetyPane.getChildren().remove(texts.lastElement());
        propetyPane.getChildren().remove(textslabel.lastElement());
        texts.remove(texts.lastElement());
        textslabel.remove(textslabel.lastElement());
        return row--;
    }

    private int addTextLine(GridPane gridPane, int row) {
        TextField url = new TextField();
        url.setPrefColumnCount(40);
        Label label = new Label("Строка текста");
        textslabel.add(label);
        propetyPane.add(label, 0, ++row);
        propetyPane.add(url, 1, row);
        texts.add(url);
        return row;
    }
}
