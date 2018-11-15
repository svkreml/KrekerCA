package svkreml.krekerCA.gui.params.extensions;

import caJava.core.extensions.extParser.ExtensionObject;
import caJava.core.extensions.extParser.KeyUsageExtensionObject;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import org.bouncycastle.asn1.x509.KeyUsage;

import java.util.HashMap;
import java.util.Map;

public class KeyUsageExtensionField implements ExtensionField {
    String name = "keyUsage";
    String discr = "2.5.29.15, Предназначение (применимость) ключа";


    CheckBox isUsed = new CheckBox();
    CheckBox isCritical = new CheckBox();
    HashMap<Integer, CheckBox> keyUsages = new HashMap<>();

    public ExtensionObject getExtensionObject() {
        Integer key = new Integer(0);
        for (Integer integerCheckBoxEntry : keyUsages.keySet()) {
            if(keyUsages.get(integerCheckBoxEntry).isSelected())
                key |= integerCheckBoxEntry;
        }

        return new KeyUsageExtensionObject(isCritical.isSelected(), key);
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


        keyUsages.put(KeyUsage.digitalSignature, new CheckBox("Цифровая подпись"));
        gridPane.add(keyUsages.get(KeyUsage.digitalSignature), 1, ++row,3,1);

        keyUsages.put(KeyUsage.nonRepudiation, new CheckBox("Неотрекаемость"));
        gridPane.add(keyUsages.get(KeyUsage.nonRepudiation), 1, ++row,3,1);

        keyUsages.put(KeyUsage.keyEncipherment, new CheckBox("Шифрование ключей"));
        gridPane.add(keyUsages.get(KeyUsage.keyEncipherment), 1, ++row,3,1);

        keyUsages.put(KeyUsage.dataEncipherment, new CheckBox("Шифрование данных"));
        gridPane.add(keyUsages.get(KeyUsage.dataEncipherment), 1, ++row,3,1);

        keyUsages.put(KeyUsage.keyAgreement, new CheckBox("Согласование ключей"));
        gridPane.add(keyUsages.get(KeyUsage.keyAgreement), 1, ++row,3,1);

        keyUsages.put(KeyUsage.keyCertSign, new CheckBox("Подписывание сертификатов"));
        gridPane.add(keyUsages.get(KeyUsage.keyCertSign), 1, ++row,3,1);

        keyUsages.put(KeyUsage.cRLSign, new CheckBox("Автономное подписание списка отзыва (CRL), Подписывание списка отзыва (CRL)"));
        gridPane.add(keyUsages.get(KeyUsage.cRLSign), 1, ++row,3,1);

        keyUsages.put(KeyUsage.encipherOnly, new CheckBox("Только шифрование"));
        gridPane.add(keyUsages.get(KeyUsage.encipherOnly), 1, ++row,3,1);

        keyUsages.put(KeyUsage.decipherOnly, new CheckBox("Только расшифровка"));
        gridPane.add(keyUsages.get(KeyUsage.decipherOnly), 1, ++row,3,1);

        return row;
    }
}
