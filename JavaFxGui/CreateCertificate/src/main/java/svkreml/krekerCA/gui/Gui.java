package svkreml.krekerCA.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import svkreml.krekerCA.CertificateGeneratorHandler;
import svkreml.krekerCA.gui.params.extensions.ExtensionField;
import svkreml.krekerCA.gui.params.extensions.PrivateKeyUsagePeriodExtensionField;

import svkreml.krekerCA.gui.params.subject.SubjectField;
import svkreml.krekerCa.gui.JavaFxUtils;

import java.util.Vector;

public class Gui {
    public GridPane gridPane;
    CheckBox selfSigned = new CheckBox("Самоподписанный сертификат");
    TextField caCertificateTF = new TextField();
    TextField caCertificatePkeyTF = new TextField();


    Button pickCaCert = new Button("...");
    Button pickCaCertPkey = new Button("...");
    TextField serialTF = new TextField();
    TextField dateFromTF = new TextField();
    TextField dateToTF = new TextField();
    TextField AlgTF = new TextField();
    CertificateGeneratorHandler certificateGeneratorHandler = new CertificateGeneratorHandler();
    Vector<ExtensionField> extensionFields = new Vector<>();
    Vector<SubjectField> subjectFields = new Vector<>();

    public void initialize() {
        System.out.println("Hello world!");
        gridPane.add(new Label("Создание Сертификата"), 0, 0, 3, 1);

        int row = 0;


        row = setCaCertificatePath(row);
        row = setBaseFields(row);
        row = setSubject(row);
        row = setExtensions(row);


        Button createCert = new Button("Создать сертификат!");
        gridPane.add(createCert, 0, ++row, 1, 1);
        gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->
                generateCertificate());
    }

    private void generateCertificate() {
        if (selfSigned.isSelected()) {
            certificateGeneratorHandler.generate(serialTF, dateFromTF, dateToTF, AlgTF, subjectFields, extensionFields);
        } else {
            certificateGeneratorHandler.generate(serialTF, dateFromTF, dateToTF, AlgTF, subjectFields, extensionFields, caCertificateTF, caCertificatePkeyTF);
        }
    }

    private int setBaseFields(int row) {
        row = row + 3;
        gridPane.add(new Label("------------------------------------------------------------"), 0, ++row, 3, 1);
        gridPane.add(new Label("Данные"), 1, ++row, 3, 1);

        gridPane.add(new Label("Серийный номер"), 0, ++row, 1, 1);
        gridPane.add(serialTF, 1, row, 1, 1);
        gridPane.add(new Label("Действителен с"), 0, ++row, 1, 1);
        gridPane.add(dateFromTF, 1, row, 1, 1);
        gridPane.add(new Label("Действителен по"), 0, ++row, 1, 1);
        gridPane.add(dateToTF, 1, row, 1, 1);
        gridPane.add(new Label("Алгоритм"), 0, ++row, 1, 1);
        gridPane.add(AlgTF, 1, row, 1, 1);
        return row;
    }

    private int setCaCertificatePath(int row) {

        pickCaCert.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->
                caCertificateTF.setText(JavaFxUtils.fileChooser("Выбор сертификата УЦ").getAbsolutePath()));
        pickCaCertPkey.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->
                caCertificatePkeyTF.setText(JavaFxUtils.fileChooser("Выбор ключа сертификата УЦ").getAbsolutePath()));
        gridPane.add(selfSigned, 0, ++row, 3, 1);
        selfSigned.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                caCertificateTF.setDisable(newValue);
                caCertificatePkeyTF.setDisable(newValue);
            }
        });

        caCertificateTF.setPromptText("caCertificateTF");
        caCertificatePkeyTF.setPromptText("caCertificatePkeyTF");

        gridPane.add(new Label("Путь к корневому сертификату"), 0, ++row, 1, 1);
        gridPane.add(caCertificateTF, 1, row, 1, 1);
        gridPane.add(pickCaCert, 2, row, 1, 1);

        gridPane.add(new Label("Путь к ключу корневого сертификата"), 0, ++row, 1, 1);
        gridPane.add(caCertificatePkeyTF, 1, row, 1, 1);
        gridPane.add(pickCaCertPkey, 2, row, 1, 1);
        return row;
    }

    private int setExtensions(int row) {
        row = row + 3;
        gridPane.add(new Label("------------------------------------------------------------"), 0, ++row, 3, 1);
        gridPane.add(new Label("Заполнение расширений"), 1, ++row, 3, 1);

        gridPane.add(new Label("OID"), 0, ++row, 1, 1);
        gridPane.add(new Label("Значение"), 1, row, 1, 1);
        gridPane.add(new Label("Критическое?  "), 10, row, 1, 1);
        gridPane.add(new Label("Использовать?  "), 11, row, 1, 1);


        extensionFields.add(new PrivateKeyUsagePeriodExtensionField());
        extensionFields.add(new PrivateKeyUsagePeriodExtensionField());
        extensionFields.add(new PrivateKeyUsagePeriodExtensionField());

        for (ExtensionField extensionField : extensionFields) {
            row = extensionField.addFieldToGridPane(gridPane, ++row);
        }


        return row;
    }

    private int setSubject(int row) {
        row = row + 3;
        gridPane.add(new Label("------------------------------------------------------------"), 0, ++row, 3, 1);
        gridPane.add(new Label("Заполнение поля Субъект"), 1, ++row + 1, 3, 1);

        gridPane.add(new Label("OID"), 0, row, 1, 1);
        gridPane.add(new Label("Значение"), 1, row, 1, 1);
        gridPane.add(new Label("Использовать?"), 11, row, 1, 1);


        subjectFields.add(new SubjectField("CN"));
        subjectFields.add(new SubjectField("C"));
        subjectFields.add(new SubjectField("C"));
        subjectFields.add(new SubjectField("C"));
        subjectFields.add(new SubjectField("C"));
        subjectFields.add(new SubjectField("C"));
        subjectFields.add(new SubjectField("C"));
        subjectFields.add(new SubjectField("C"));
        for (SubjectField subjectField : subjectFields) {
            subjectField.addFieldToGridPane(gridPane, ++row);
        }
        return row;
    }


}
