package svkreml.krekerCA.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import svkreml.krekerCA.CertificateGeneratorHandler;
import svkreml.krekerCA.CrlGeneratorHandler;
import svkreml.krekerCA.gui.params.extensions.*;
import svkreml.krekerCA.gui.params.subject.SubjectField;
import svkreml.krekerCA.gui.params.subject.SubjectOrder;
import svkreml.krekerCa.gui.JavaFxUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

public class Gui {
    public GridPane gridPane;
    public GridPane gridPaneCrl;
    CheckBox selfSigned = new CheckBox("Самоподписанный сертификат");
    TextField caCertificateTF = new TextField();
    TextField caCertificatePkeyTF = new TextField();


    Button pickCaCert = new Button("...");
    Button pickCaCertPkey = new Button("...");
    TextField serialTF = new TextField("123123123");
    DatePicker dateFromDP = new DatePicker();
    DatePicker dateToDP = new DatePicker();
    ChoiceBox<String> algsCB = new ChoiceBox<String>();
    CertificateGeneratorHandler certificateGeneratorHandler = new CertificateGeneratorHandler();
    Vector<ExtensionField> extensionFields = new Vector<>();
    Vector<SubjectField> subjectFields = new Vector<>();

    public void initialize() {
        algsCB.getItems().addAll("gost2012_256", "gost2012_512", "gost2001", "rsa2048", "rsa4096");

        CrlGeneratorHandler crlGeneratorHandler = new CrlGeneratorHandler(gridPaneCrl,  caCertificateTF, caCertificatePkeyTF);
        crlGeneratorHandler.addCrlField();

        int row = 0;
        initCreator(row);
    }


    private int initCreator(int row) {
        gridPane.setPrefWidth(1000);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(30);
        gridPane.getColumnConstraints().addAll(col1, col2, col3);


        System.out.println("Hello world!");
        //gridPane.setHgap(50);
        // gridPane.setVgap(0);
        gridPane.add(new Label("Создание Сертификата"), 0, 0, 3, 1);


        row = setCaCertificatePath(row);
        row = setBaseFields(row);
        row = setSubject(row);
        row = setExtensions(row);


        Button createCert = new Button("Создать сертификат!");
        gridPane.add(createCert, 0, ++row, 1, 1);
        createCert.setOnAction(this::generateCertificate);
        return row;
    }

    private void generateCertificate(ActionEvent event) {
        System.out.println("Нажата кнопка генерации сертификата");
        try {
            certificateGeneratorHandler.generate(serialTF, dateFromDP.getValue(), dateToDP.getValue(), algsCB.getValue(), subjectFields, extensionFields, selfSigned, caCertificateTF, caCertificatePkeyTF);
        } catch (Exception e) {
            e.printStackTrace();
            String stackTrace = getStackTrace(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle(e.getMessage());
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText(stackTrace);
            alert.getDialogPane().setMinWidth(800);
            alert.getDialogPane().setMaxHeight(800);
            alert.showAndWait();
        }

    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String s = sw.toString();
        return s;
    }

    private int setBaseFields(int row) {
        // row = row + 3;
        gridPane.add(new Label("------------------------------------------------------------------------------------------------------------------------"), 0, ++row, 10, 1);
        gridPane.add(new Label("Данные"), 1, ++row, 3, 1);

        gridPane.add(new Label("Серийный номер"), 0, ++row, 1, 1);
        gridPane.add(serialTF, 1, row, 1, 1);
        gridPane.add(new Label("Действителен с"), 0, ++row, 1, 1);
        gridPane.add(dateFromDP, 1, row, 1, 1);
        gridPane.add(new Label("Действителен по"), 0, ++row, 1, 1);
        gridPane.add(dateToDP, 1, row, 1, 1);
        gridPane.add(new Label("Алгоритм"), 0, ++row, 1, 1);
        gridPane.add(algsCB, 1, row, 1, 1);
        return row;
    }

    private int setCaCertificatePath(int row) {
        row = row + 1;
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
        gridPane.add(selfSigned, 0, ++row, 3, 1);
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
        gridPane.add(new Label("Путь к УЦ сертификату"), 0, ++row, 1, 1);
        gridPane.add(caCertificateTF, 1, row, 1, 1);
        gridPane.add(pickCaCert, 2, row, 1, 1);
        row = row + 1;
        gridPane.add(new Label("Путь к ключу УЦ сертификата"), 0, ++row, 1, 1);
        gridPane.add(caCertificatePkeyTF, 1, row, 1, 1);
        gridPane.add(pickCaCertPkey, 2, row, 1, 1);
        return row;
    }

    private int setExtensions(int row) {
        row = row + 3;
        gridPane.add(new Label("------------------------------------------------------------------------------------------------------------------------"), 0, ++row, 10, 1);
        gridPane.add(new Label("Заполнение расширений"), 1, ++row, 3, 1);

        gridPane.add(new Label("OID"), 0, ++row, 1, 1);
        gridPane.add(new Label("Значение"), 1, row, 1, 1);
        gridPane.add(new Label("Критическое?  "), 10, row, 1, 1);
        gridPane.add(new Label("Использовать?  "), 11, row, 1, 1);


        extensionFields.add(new PrivateKeyUsagePeriodExtensionField());
        extensionFields.add(new CRLDistributionPointsExtensionField());
        extensionFields.add(new AuthorityKeyIdentifierExtensionField());
        extensionFields.add(new KeyUsageExtensionField());
        extensionFields.add(new SubjectSignToolExtensionField());
        extensionFields.add(new IssuerSignToolExtensionField());
        extensionFields.add(new CertificatePoliciesExtensionField());
        extensionFields.add(new ExtendedKeyUsageExtensionField());
        extensionFields.add(new AuthorityInfoAccessExtensionField());
        extensionFields.add(new BasicConstraintsExtensionField());
        extensionFields.add(new SubjectKeyIdentifierExtensionField());


        for (ExtensionField extensionField : extensionFields) {
            row = extensionField.addFieldToGridPane(gridPane, row);
        }


        return row;
    }

    private int setSubject(int row) {
        gridPane.add(new Label("------------------------------------------------------------------------------------------------------------------------"), 0, ++row, 10, 1);
        gridPane.add(new Label("Заполнение поля Субъект"), 1, ++row, 3, 1);

        gridPane.add(new Label("OID"), 0, ++row, 1, 1);
        gridPane.add(new Label("Значение"), 1, row, 1, 1);
        gridPane.add(new Label("Использовать?"), 11, row, 1, 1);
        new SubjectOrder().getSubjects().forEach(
                (k, v) -> {
                    subjectFields.add(new SubjectField(k, v));
                }
        );

        for (SubjectField subjectField : subjectFields) {
            subjectField.addFieldToGridPane(gridPane, ++row);
        }
        return row;
    }


}
