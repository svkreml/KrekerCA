package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.CRLDistributionPointsObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Vector;

public class CRLDistributionPointsExtensionField implements ExtensionField {
    String name = "cRLDistributionPoints";
    String discr = "2.5.29.31 – Точки распределения списков отзыва (CRL)";

    Vector<TextField> urls = new Vector<>();


    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();
    int rowOfUrl;

    public ExtensionObject getExtensionObject() {
        String[] urlsArray = new String[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            urlsArray[i] = urls.elementAt(i).getText();
        }
        return new CRLDistributionPointsObject(isCritical.isSelected(), urlsArray);
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
    Vector<Label> textslabel = new Vector<>();
    private int removeTextLine(GridPane gridPane, int row) {
        gridPane.getChildren().remove(urls.lastElement());
        gridPane.getChildren().remove(textslabel.lastElement());
        urls.remove(urls.lastElement());
        textslabel.remove( textslabel.lastElement());
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
