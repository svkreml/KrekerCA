package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.TemplateNameExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Vector;

public class TemplateNameExtensionField extends BaseExtensionField {

    public TemplateNameExtensionField(String name, String discr) {
        super("templateName", EXTENSION_IDENTIFIER_ID+", Сведения о шаблоне сертифката");
    }

    public void reset() {
        row = 0;
        propetyPane.getChildren().removeAll();
        addFieldToGridPane();
    }


    private final static String EXTENSION_IDENTIFIER_ID = TemplateNameExtensionObject.EXTENSION_IDENTIFIER.getId();
    Vector<TextField> texts = new Vector<>();
    Vector<Label> textslabel = new Vector<>();
    int rowOfText;
    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();

    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    @Override
    public void setFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) throws IOException {

    }

    public ExtensionObject buildExtensionObject() {
        String[] urlsArray = new String[texts.size()];
        for (int i = 0; i < texts.size(); i++) {
            urlsArray[i] = texts.elementAt(i).getText();
        }
        return new TemplateNameExtensionObject(isCritical.isSelected(), urlsArray);
    }

    @Override
    public boolean getIsUsedCheckBox() {
        return isUsed.isSelected();
    }

    @Override
    public void setIsUsedCheckBox(Boolean value) {

    }

    @Override
    public Node getGui() {
        return null;
    }

    public int addFieldToGridPane() {
        gridPane.add(new Separator(), 0, ++row, 12, 1);
        gridPane.add(isCritical, 10, ++row);
        gridPane.add(isUsed, 11, row);
        gridPane.add(new Label(name), 0, row);
        gridPane.add(new Label(description), 1, row, 4, 1);
        row = addTextLine();

        rowOfText = row;
        row = row + 20;
        Button add = new Button("ещё");
        gridPane.add(add, 0, ++row);
        add.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            rowOfText = addTextLine();
        });
        Button remove = new Button("удалить");
        gridPane.add(remove, 0, ++row);
        remove.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            rowOfText = removeTextLine();
        });
        return row;
    }

    private int removeTextLine() {
        gridPane.getChildren().remove(texts.lastElement());
        gridPane.getChildren().remove(textslabel.lastElement());
        texts.remove(texts.lastElement());
        textslabel.remove(textslabel.lastElement());
        return row--;
    }

    private int addTextLine(  ) {
        TextField url = new TextField();
        Label label = new Label("Строка текста");
        textslabel.add(label);
        gridPane.add(label, 1, ++row);
        gridPane.add(url, 2, row);
        texts.add(url);
        return row;
    }
}
