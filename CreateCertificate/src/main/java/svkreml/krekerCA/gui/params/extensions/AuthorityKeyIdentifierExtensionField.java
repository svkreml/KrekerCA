package svkreml.krekerCA.gui.params.extensions;

import svkreml.krekerCa.core.extensions.extParser.AuthorityKeyIdentifierObject;
import svkreml.krekerCa.core.extensions.extParser.ExtensionObject;
import javafx.scene.Node;

import java.security.cert.X509Certificate;

public class AuthorityKeyIdentifierExtensionField extends BaseExtensionField  {
    private final static String EXTENSION_IDENTIFIER_ID = AuthorityKeyIdentifierObject.EXTENSION_IDENTIFIER.getId();
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER_ID;
    }

    public void reset(){
    }
    public AuthorityKeyIdentifierExtensionField() {
        super( "authorityKeyIdentifier", EXTENSION_IDENTIFIER_ID+", идентификатор ключа центра сертификатов");
    }

    @Override
    public void innerMethodSetFields(X509Certificate donorCert, X509Certificate caCert, boolean isCritical) {

    }


    @Override
    public Node getGui() {
        return gridPane;
    }



    public ExtensionObject buildExtensionObject() {
        return new AuthorityKeyIdentifierObject(isCriticalCheckBox.isSelected());
    }
}
