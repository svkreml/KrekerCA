package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.CRLDistributionPointsObject;
import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Vector;

public class CRLDistributionPointsExtensionField extends BaseExtensionField {

    public void reset(){
        row = 0;
        propetyPane.getChildren().removeAll();
        urls.clear();
        urlLines.getChildren().clear();
        addFieldToGridPane();
    }


    private final static String EXTENSION_IDENTIFIER_ID = CRLDistributionPointsObject.EXTENSION_IDENTIFIER.getId();
    ArrayList<HBox> urls = new ArrayList<HBox>();
    VBox urlLines = new VBox();


    public CRLDistributionPointsExtensionField() {
        super("cRLDistributionPoints", "2.5.29.31 – Точки распределения списков отзыва (CRL)");
    }

    private void initTable(int row) {


    }

    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) throws IOException {
        urls.clear();

        final byte[] extensionValue = donorCert.getExtensionValue(EXTENSION_IDENTIFIER_ID);
        final CRLDistPoint crlDistPoint = CRLDistPoint.getInstance(JcaX509ExtensionUtils.parseExtensionValue(extensionValue));
        for (DistributionPoint distributionPoint : crlDistPoint.getDistributionPoints()) {
            addUrl(GeneralNames.getInstance(distributionPoint.getDistributionPoint().getName()).getNames()[0].getName().toString());
        }
    }

    public ExtensionObject buildExtensionObject() {

        String[] urlsArray = new String[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            urlsArray[i] = ((TextField) urls.get(i).getChildren().get(1)).getText();
        }
        return new CRLDistributionPointsObject(isCriticalCheckBox.isSelected(), urlsArray);
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
        gridPane.add(urlLines,0,10);
        row = row + 20;
        initTable(row);
        Button button = new Button("ещё");
        propetyPane.add(button, 0, ++row);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            addUrl(null);
        });

        return row;
    }

    private void addUrl(String value) {
        TextField url = new TextField();
        url.setPrefColumnCount(40);

        if(value !=null)
            url.setText(value);

        HBox hBox = new HBox();
        Button remove = new Button("удалить");
        Label label = new Label("URL");
        hBox.getChildren().addAll(label, url, remove);

        urlLines.getChildren().add(hBox);
        urls.add(hBox);
        remove.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                urls.remove(hBox);
                urlLines.getChildren().remove(hBox);
        });

    }
}
/*
class Line{
    VBox vBox;
    TextField url;

    public Line(VBox vBox, TextField url) {
        this.vBox = vBox;
        this.url = url;
    }

    public VBox getvBox() {
        return vBox;
    }

    public TextField getUrl() {
        return url;
    }
}*/
