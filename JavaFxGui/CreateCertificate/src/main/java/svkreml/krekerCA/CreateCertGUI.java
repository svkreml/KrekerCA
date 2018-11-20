package svkreml.krekerCA;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import svkreml.krekerCA.gui.params.extensions.*;
import svkreml.krekerCA.gui.params.subject.SubjectField;
import svkreml.krekerCA.gui.params.subject.SubjectOrder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

public class CreateCertGUI {

    GridPane createCertPane = new GridPane();


    TextField serialTF = new TextField("123123123");
    DatePicker dateFromDP = new DatePicker();
    DatePicker dateToDP = new DatePicker();
    ChoiceBox<String> algsCB = new ChoiceBox<String>();
    CertificateGeneratorHandler certificateGeneratorHandler = new CertificateGeneratorHandler();
    Vector<ExtensionField> extensionFields = new Vector<>();
    Vector<SubjectField> subjectFields = new Vector<>();
    CertPath certPath = new CertPath();

    public Tab initCreator() {
        Tab createCertTab = new Tab("Создать сертификат");

        int row = 5;

        createCertPane.setPadding(new Insets(10, 10, 10, 10));
        createCertPane.add(certPath.initPath(), 0, 0, 20, 6);


        algsCB.getItems().addAll("gost2012_256", "gost2012_512", "gost2001", "rsa2048", "rsa4096");
        createCertPane.setPrefWidth(1000);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(20);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(30);
        createCertPane.getColumnConstraints().addAll(col1, col2, col3);


        System.out.println("Hello world!");
        //certPathPane.setHgap(50);
        // certPathPane.setVgap(0);
        createCertPane.add(new Label("Создание Сертификата"), 0, ++row, 3, 1);

        row = setBaseFields(row);
        row = setSubject(row);
        row = setExtensions(row);

        Button createCert = new Button("Создать сертификат!");
        createCert.setPadding(new Insets(5, 10, 5, 10));
        createCert.setAlignment(Pos.CENTER_RIGHT);
        createCert.setOnAction(this::generateCertificate);

        VBox tabRootBox = new VBox();
        tabRootBox.getChildren().add(new ScrollPane(createCertPane));
        tabRootBox.getChildren().add(createCert);

        createCertTab.setContent(tabRootBox);

        return createCertTab;
    }

    private void generateCertificate(ActionEvent event) {
        System.out.println("Нажата кнопка генерации сертификата");
        try {
            certificateGeneratorHandler.generate(serialTF, dateFromDP.getValue(), dateToDP.getValue(), algsCB.getValue(), subjectFields, extensionFields, certPath.getSelfSigned(), certPath.getCaCertificateTF(), certPath.getCaCertificatePkeyTF());
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
        createCertPane.add(new Separator(), 0, ++row, 10, 1);
        createCertPane.add(new Label("Данные"), 1, ++row, 3, 1);

        createCertPane.add(new Label("Серийный номер"), 0, ++row, 1, 1);
        createCertPane.add(serialTF, 1, row, 1, 1);
        createCertPane.add(new Label("Действителен с"), 0, ++row, 1, 1);
        createCertPane.add(dateFromDP, 1, row, 1, 1);
        createCertPane.add(new Label("Действителен по"), 0, ++row, 1, 1);
        createCertPane.add(dateToDP, 1, row, 1, 1);
        createCertPane.add(new Label("Алгоритм"), 0, ++row, 1, 1);
        createCertPane.add(algsCB, 1, row, 1, 1);
        return row;
    }


    private int setExtensions(int row) {
        row = row + 3;
        createCertPane.add(new Separator(), 0, ++row, 10, 1);
        createCertPane.add(new Label("Заполнение расширений"), 1, ++row, 3, 1);


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

        row = row + 20;
        for (ExtensionField extensionField : extensionFields) {
            createCertPane.add(extensionField.getGui(), 0, row, 15, 1);
            row = row + 20;
        }


        return row;
    }

    private int setSubject(int row) {
        createCertPane.add(new Separator(), 0, ++row, 10, 1);
        createCertPane.add(new Label("Заполнение поля Субъект"), 1, ++row, 3, 1);

        createCertPane.add(new Label("OID"), 0, ++row, 1, 1);
        createCertPane.add(new Label("Значение"), 1, row, 1, 1);
        createCertPane.add(new Label("Использовать?"), 11, row, 1, 1);
        new SubjectOrder().getSubjects().forEach(
                (k, v) -> {
                    subjectFields.add(new SubjectField(k, v));
                }
        );

        for (SubjectField subjectField : subjectFields) {
            subjectField.addFieldToGridPane(createCertPane, ++row);
        }
        return row;
    }
}