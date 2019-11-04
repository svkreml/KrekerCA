package svkreml.krekerCA;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import svkreml.krekerCA.utils.JavaFxUtils;

public class CertPath {
    public void hideCheckBox(){
        selfSigned.setVisible(false);
    }
    CheckBox selfSigned = new CheckBox("Самоподписанный сертификат");
    TextField caCertificateTF = new TextField();

    public CheckBox getSelfSigned() {
        return selfSigned;
    }

    public TextField getCaCertificateTF() {
        return caCertificateTF;
    }

    public TextField getCaCertificatePkeyTF() {
        return caCertificatePkeyTF;
    }

    TextField caCertificatePkeyTF = new TextField();
    Button pickCaCert = new Button("...");
    Button pickCaCertPkey = new Button("...");
   GridPane certPathPane = new GridPane();

    public Pane initPath() {
        int row=0;
        pickCaCert.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            try {
                caCertificateTF.setText(JavaFxUtils.fileChooser("Выбор сертификата УЦ").getAbsolutePath());
            } catch (NullPointerException e) {
                System.out.println("нажата кнопка отмены при выборе файла");
            }
        });
        pickCaCertPkey.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            try {
                caCertificatePkeyTF.setText(JavaFxUtils.fileChooser("Выбор ключа сертификата УЦ").getAbsolutePath());
            } catch (NullPointerException e) {
                System.out.println("нажата кнопка отмены при выборе файла");
            }
        });
        certPathPane.add(selfSigned, 0, ++row, 3, 1);
        selfSigned.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                caCertificateTF.setDisable(newValue);
                caCertificatePkeyTF.setDisable(newValue);
            }
        });
        row = row + 1;
        caCertificateTF.setPromptText("caCertificateTF");
        caCertificatePkeyTF.setPromptText("caCertificatePkeyTF");
        row = row + 1;
        certPathPane.add(new Label("Путь к УЦ сертификату"), 0, ++row, 1, 1);
        certPathPane.add(caCertificateTF, 1, row, 1, 1);
        certPathPane.add(pickCaCert, 2, row, 1, 1);
        row = row + 1;
        certPathPane.add(new Label("Путь к ключу УЦ сертификата"), 0, ++row, 1, 1);
        certPathPane.add(caCertificatePkeyTF, 1, row, 1, 1);
        certPathPane.add(pickCaCertPkey, 2, row, 1, 1);
        return certPathPane;
    }
}
