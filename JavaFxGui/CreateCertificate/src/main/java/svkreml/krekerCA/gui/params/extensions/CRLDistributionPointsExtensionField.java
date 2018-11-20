package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.CRLDistributionPointsObject;
import caJava.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Vector;

public class CRLDistributionPointsExtensionField extends BaseExtensionField {


    Vector<TextField> urls = new Vector<>();

    int rowOfUrl;
    Vector<Label> textslabel = new Vector<>();

    public CRLDistributionPointsExtensionField() {
        super("cRLDistributionPoints", "2.5.29.31 – Точки распределения списков отзыва (CRL)");
    }

    public ExtensionObject getExtensionObject() {

        String[] urlsArray = new String[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            urlsArray[i] = urls.elementAt(i).getText();
        }
        return new CRLDistributionPointsObject(isCritical.isSelected(), urlsArray);
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
        row = addUrl(gridPane, row++);

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
            if (!urls.isEmpty())
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
