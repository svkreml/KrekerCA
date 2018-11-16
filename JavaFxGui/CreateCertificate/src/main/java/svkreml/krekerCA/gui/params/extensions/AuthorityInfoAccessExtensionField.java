package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.AuthorityInfoAccessExtensionObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Vector;

public class AuthorityInfoAccessExtensionField implements ExtensionField {
    String name = "authorityInfoAccess";
    String discr = "1.3.6.1.5.5.7.1.1, доступ к информации о центрах сертификации";

    Vector<TextField> urls = new Vector<>();

    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();
    int rowOfUrl;
    Vector<Label> textslabel = new Vector<>();

    public ExtensionObject getExtensionObject() {
        String[] urlsArray = new String[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            urlsArray[i] = urls.elementAt(i).getText();
        }
        return new AuthorityInfoAccessExtensionObject(isCritical.isSelected(), urlsArray, new String[]{});
    }

    @Override
    public boolean getIsUsed() {
        return isUsed.isSelected();
    }

    public int addFieldToGridPane(GridPane gridPane, int row) {
        gridPane.add(new Separator(), 0, ++row, 12, 1);
        gridPane.add(isCritical, 10, ++row);
        gridPane.add(isUsed, 11, row);
        gridPane.add(new Label(name), 0, row);
        gridPane.add(new Label(discr), 1, row, 4, 1);
        row = addUrl(gridPane, row);

        rowOfUrl = row;
        row = row + 20;
        Button button = new Button("ещё");
        gridPane.add(button, 0, ++row);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            rowOfUrl = addUrl(gridPane, rowOfUrl);
        });
        Button remove = new Button("удалить");
        gridPane.add(remove, 0, ++row);
        remove.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            rowOfUrl = removeTextLine(gridPane, rowOfUrl);
        });
        return row;
    }

    private int removeTextLine(GridPane gridPane, int row) {
        gridPane.getChildren().remove(urls.lastElement());
        gridPane.getChildren().remove(textslabel.lastElement());
        urls.remove(urls.lastElement());
        textslabel.remove(textslabel.lastElement());
        return row--;
    }

    private int addUrl(GridPane gridPane, int row) {
        TextField url = new TextField();
        Label label = new Label("URL");
        textslabel.add(label);
        gridPane.add(label, 1, ++row);
        gridPane.add(url, 2, row);
        urls.add(url);
        return row;
    }
}
