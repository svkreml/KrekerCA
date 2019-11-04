package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.AuthorityInfoAccessExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.security.cert.X509Certificate;
import java.util.Vector;

public class AuthorityInfoAccessExtensionField extends BaseExtensionField {
    public final static String EXTENSION_IDENTIFIER_ID = AuthorityInfoAccessExtensionObject.EXTENSION_IDENTIFIER.getId();
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    public AuthorityInfoAccessExtensionField() {
        super("authorityInfoAccess",EXTENSION_IDENTIFIER_ID+", доступ к информации о центрах сертификации");
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }

    public void reset(){
        row = 0;
        propetyPane.getChildren().removeAll();
        urls.clear();
        addFieldToGridPane();
    }
    private Vector<TextField> urls = new Vector<>();


    private int rowOfUrl;
    private Vector<Label> textslabel = new Vector<>();

    public ExtensionObject buildExtensionObject() {
        String[] urlsArray = new String[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            urlsArray[i] = urls.elementAt(i).getText();
        }
        return new AuthorityInfoAccessExtensionObject(isCriticalCheckBox.isSelected(), urlsArray, new String[]{});
    }


    @Override
    public Node getGui() {
        addFieldToGridPane();
        return gridPane;
    }
    @Override
    public boolean getIsUsedCheckBox() {
        return isUsedCheckBox.isSelected();
    }

    public int addFieldToGridPane() {
        row = addUrl(gridPane, row);
        rowOfUrl = row;
        row = row + 20;
        Button button = new Button("ещё");
        propetyPane.add(button, 0, ++row);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            rowOfUrl = addUrl(gridPane, rowOfUrl);
        });
        Button remove = new Button("удалить");
        propetyPane.add(remove, 0, ++row);
        remove.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if(!urls.isEmpty())
            rowOfUrl = removeTextLine(gridPane, rowOfUrl);
        });
        return row;
    }

    private int removeTextLine(GridPane gridPane, int row) {
        propetyPane.getChildren().remove(urls.lastElement());
        propetyPane.getChildren().remove(textslabel.lastElement());
        urls.remove(urls.lastElement());
        textslabel.remove(textslabel.lastElement());
        return row--;
    }

    private int addUrl(GridPane gridPane, int row) {
        TextField url = new TextField();
        url.setPrefColumnCount(40);
        Label label = new Label("URL");
        textslabel.add(label);
        propetyPane.add(label, 0, ++row);
        propetyPane.add(url, 1, row);
        urls.add(url);
        return row;
    }
}
