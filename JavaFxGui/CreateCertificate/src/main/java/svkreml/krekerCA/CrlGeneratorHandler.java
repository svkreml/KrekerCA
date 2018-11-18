package svkreml.krekerCA;

import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.CryptoAlgFactory;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.security.auth.x500.X500Principal;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public class CrlGeneratorHandler {
    Vector<TextField> serials = new Vector<>();
    Vector<DatePicker> dateOfAdds = new Vector<>();
    Vector<ChoiceBox<String>> reasons = new Vector<>();
    Vector<Label> textslabel = new Vector<>();
    int rowOfText = 2;

    TextField crlSerial = new TextField("1");
    DatePicker timeOfLife = new DatePicker(LocalDate.now());
    DatePicker createDate = new DatePicker();
    GridPane gridPaneCrl = new GridPane();
    CertPath certPath = new CertPath();
    public CrlGeneratorHandler() {

        certPath.hideCheckBox();



        reasonCrl.put("unspecified",CRLReason.unspecified);
        reasonCrl.put("keyCompromise",CRLReason.keyCompromise);
        reasonCrl.put("cACompromise",CRLReason.cACompromise);
        reasonCrl.put("affiliationChanged",CRLReason.affiliationChanged);
        reasonCrl.put("superseded",CRLReason.superseded);
        reasonCrl.put("cessationOfOperation",CRLReason.cessationOfOperation);
        reasonCrl.put("certificateHold",CRLReason.certificateHold);
        reasonCrl.put("removeFromCRL",CRLReason.removeFromCRL);
        reasonCrl.put("privilegeWithdrawn",CRLReason.privilegeWithdrawn);
        reasonCrl.put("aACompromise",CRLReason.aACompromise);
    }

    public void generate() throws Exception {
        File ca = new File(certPath.getCaCertificatePkeyTF().getText());
        File caPkey = new File(certPath.getCaCertificatePkeyTF().getText());
        byte[] bytes = FileManager.read(ca);
        X509Certificate caCert = CertEnveloper.decodeCert(bytes);
        PrivateKey privateKey = CertEnveloper.decodePrivateKey(caPkey);
        CryptoAlg cryptoAlg = CryptoAlgFactory.getInstance(
                caCert.getSigAlgName());

        X509v2CRLBuilder builder = new X509v2CRLBuilder(
                new X500Name(caCert.getIssuerX500Principal().getName()),
                Date.from(createDate.getValue().atStartOfDay(ZoneOffset.UTC).toInstant())
        );
        builder.setNextUpdate(Date.from(timeOfLife.getValue().atStartOfDay(ZoneOffset.UTC).toInstant()));
        GeneralName generalName = new GeneralName(new X500Name(caCert.getSubjectX500Principal().getName(X500Principal.RFC2253)));
        GeneralNames generalNames = new GeneralNames(generalName);
        builder.addExtension(Extension.authorityKeyIdentifier, false,  new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(caCert.getPublicKey(), generalNames, caCert.getSerialNumber()));
        builder.addExtension(Extension.cRLNumber, false, new CRLNumber(new BigInteger( crlSerial.getText(),16)));

        for (int i = 0; i < serials.size(); i++) {
            builder.addCRLEntry(new BigInteger(serials.elementAt(i).getText(),16),
                    Date.from(dateOfAdds.elementAt(i).getValue().atStartOfDay(ZoneOffset.UTC).toInstant()),
                    reasonCrl.get(reasons.elementAt(i).getValue()));
        }


        JcaContentSignerBuilder contentSignerBuilder =
                new JcaContentSignerBuilder(cryptoAlg.signatureAlgorithm);

        contentSignerBuilder.setProvider("BC");

        X509CRLHolder crlHolder = builder.build(contentSignerBuilder.build(privateKey));

        JcaX509CRLConverter converter = new JcaX509CRLConverter();
        converter.setProvider("BC");


        File output = new File("outputCerts/" +getThumbprint(caCert));
        FileManager.write(new File(output.getAbsoluteFile() + ".crl"),converter.getCRL(crlHolder).getEncoded());
    }

    public Tab initCrl() {
        Tab createCrlTab = new Tab("Создать список отзыва");
int row = 6;
        gridPaneCrl.add(certPath.initPath(), 0, 0, 20, 6);
        gridPaneCrl.add(new Separator(), 0, ++row);
        gridPaneCrl.add(new Label("Время создания"), 0, ++row);
        gridPaneCrl.add(createDate, 1, row);
        gridPaneCrl.add(new Label("Время жизни CRL"), 0, ++row);
        gridPaneCrl.add(timeOfLife, 1, row);
        gridPaneCrl.add(new Label("Serial CRL"), 0, ++row);
        gridPaneCrl.add(crlSerial, 1, row);

        Button gen = new Button("Создать");
        gridPaneCrl.add(gen, 0, 210);
        gen.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            try {
                generate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        Button add = new Button("ещё");
        gridPaneCrl.add(add, 0, 200);
        add.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            rowOfText = addTextLine(rowOfText);
        });
        Button remove = new Button("удалить");
        gridPaneCrl.add(remove, 0, 201);
        remove.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (serials.size() > 0)
                rowOfText = removeTextLine(rowOfText);
        });
        createCrlTab.setContent(gridPaneCrl);
        return createCrlTab;
    }

    private int removeTextLine(int row) {

        gridPaneCrl.getChildren().remove(serials.lastElement());
        gridPaneCrl.getChildren().remove(textslabel.lastElement());
        gridPaneCrl.getChildren().remove(reasons.lastElement());
        gridPaneCrl.getChildren().remove(dateOfAdds.lastElement());
        serials.remove(serials.lastElement());
        textslabel.remove(textslabel.lastElement());
        dateOfAdds.remove(dateOfAdds.lastElement());
        reasons.remove(reasons.lastElement());
        return row--;
    }
    HashMap<String,Integer> reasonCrl = new HashMap<>();
    private int addTextLine(int row) {


        ChoiceBox<String> reasonCB = new ChoiceBox<>();
        reasonCB.getItems().addAll(reasonCrl.keySet());
        TextField url = new TextField();
        Label label = new Label("Серийный номер: ");
        textslabel.add(label);
        gridPaneCrl.add(label, 1, ++row);
        gridPaneCrl.add(url, 2, row);
        gridPaneCrl.add(reasonCB, 3, row);
        DatePicker dateOfAdd1 = new DatePicker();
        gridPaneCrl.add(dateOfAdd1, 5, row);


        serials.add(url);
        reasons.add(reasonCB);
        dateOfAdds.add(dateOfAdd1);

        return row;
    }
    private static String getThumbprint(X509Certificate cert)
            throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        String digestHex = DatatypeConverter.printHexBinary(digest);
        return digestHex.toLowerCase();
    }
}
