package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import svkreml.krekerCa.core.extensions.extParser.KeyUsageExtensionObject;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import org.bouncycastle.asn1.x509.KeyUsage;

import java.security.cert.X509Certificate;
import java.util.HashMap;

public class KeyUsageExtensionField extends BaseExtensionField {
    public void reset() {
        row = 0;
        propetyPane.getChildren().removeAll();
        addFieldToGridPane();
    }





    private final static String EXTENSION_IDENTIFIER_ID = KeyUsageExtensionObject.EXTENSION_IDENTIFIER.getId();
    HashMap<Integer, CheckBox> keyUsages = new HashMap<>();

    public KeyUsageExtensionField() {
        super("keyUsage", EXTENSION_IDENTIFIER_ID+", Предназначение (применимость) ключа");
    }

    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }

    public ExtensionObject buildExtensionObject() {
        Integer key = new Integer(0);
        for (Integer integerCheckBoxEntry : keyUsages.keySet()) {
            if (keyUsages.get(integerCheckBoxEntry).isSelected())
                key |= integerCheckBoxEntry;
        }

        return new KeyUsageExtensionObject(isCriticalCheckBox.isSelected(), key);
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

    public int addFieldToGridPane( ) {

        keyUsages.put(KeyUsage.digitalSignature, new CheckBox("Цифровая подпись"));
        propetyPane.add(keyUsages.get(KeyUsage.digitalSignature), 0, ++row);

        keyUsages.put(KeyUsage.nonRepudiation, new CheckBox("Неотрекаемость"));
        propetyPane.add(keyUsages.get(KeyUsage.nonRepudiation), 0, ++row);

        keyUsages.put(KeyUsage.keyEncipherment, new CheckBox("Шифрование ключей"));
        propetyPane.add(keyUsages.get(KeyUsage.keyEncipherment), 0, ++row);

        keyUsages.put(KeyUsage.dataEncipherment, new CheckBox("Шифрование данных"));
        propetyPane.add(keyUsages.get(KeyUsage.dataEncipherment), 0, ++row);

        keyUsages.put(KeyUsage.keyAgreement, new CheckBox("Согласование ключей"));
        propetyPane.add(keyUsages.get(KeyUsage.keyAgreement), 0, ++row);

        keyUsages.put(KeyUsage.keyCertSign, new CheckBox("Подписывание сертификатов"));
        propetyPane.add(keyUsages.get(KeyUsage.keyCertSign), 0, ++row);

        keyUsages.put(KeyUsage.cRLSign, new CheckBox("Автономное подписание списка отзыва (CRL), Подписывание списка отзыва (CRL)"));
        propetyPane.add(keyUsages.get(KeyUsage.cRLSign), 0, ++row);

        keyUsages.put(KeyUsage.encipherOnly, new CheckBox("Только шифрование"));
        propetyPane.add(keyUsages.get(KeyUsage.encipherOnly), 0, ++row);

        keyUsages.put(KeyUsage.decipherOnly, new CheckBox("Только расшифровка"));
        propetyPane.add(keyUsages.get(KeyUsage.decipherOnly), 0, ++row);

        return row;
    }
}
