package svkreml.krekerCA;

import caJava.core.CertAndKey;
import caJava.core.creator.CertificateCreator;
import caJava.core.cryptoAlg.CryptoAlg;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2001;
import caJava.core.cryptoAlg.impl.CryptoAlgGost2012_256;
import caJava.core.cryptoAlg.impl.CryptoRSA;
import caJava.core.extensions.ExtensionParam;
import caJava.core.extensions.ExtensionWrapper;
import caJava.core.extensions.extParser.ExtensionObject;
import caJava.customOID.CustomBCStyle;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.operator.OperatorCreationException;
import svkreml.krekerCA.gui.params.extensions.ExtensionField;
import svkreml.krekerCA.gui.params.subject.SubjectField;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Vector;

public class CertificateGeneratorHandler {


    private static String getThumbprint(X509Certificate cert)
            throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        String digestHex = DatatypeConverter.printHexBinary(digest);
        return digestHex.toLowerCase();
    }

    public void generate(TextField serialTF, TextField dateFromTF, TextField dateToTF, TextField algTF, Vector<SubjectField> subjectFields, Vector<ExtensionField> extensionFields, CheckBox selfSigned, TextField caCertificateTF, TextField caCertificatePkeyTF) throws Exception {
        String alg = algTF.getText();
        CryptoAlg cryptoAlg;
        switch (alg) {
            case "gost2012_256":
                cryptoAlg = CryptoAlgGost2012_256.getCryptoAlg();
                break;
            case "gost2001":
                cryptoAlg = CryptoAlgGost2001.getCryptoAlg();
                break;
            case "rsa2048":
                cryptoAlg = CryptoRSA.getCryptoAlg(2048);
                break;
            case "rsa4096":
                cryptoAlg = CryptoRSA.getCryptoAlg(4096);
                break;
            default:
                throw new IllegalArgumentException("Ошибка в значении аргумента alg");
        }
        CertificateCreator certificateCreator = new CertificateCreator(cryptoAlg);

        CertAndKey certAndKey;
        Date dateFrom = new Date(dateFromTF.getText());
        Date dateTo = new Date(dateToTF.getText());


        X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);
        for (int i = subjectFields.size() - 1; i >= 0; i--) {
            SubjectField value = subjectFields.elementAt(i);
            if (value.getIsUsed())
                x500NameBld.addRDN((ASN1ObjectIdentifier) CustomBCStyle.DefaultLookUp.get(value.getName().toLowerCase()), value.getTextField());
        }

        Vector<ExtensionObject> extensionParams = new Vector<>();
        for (ExtensionField extensionField : extensionFields) {
            if (extensionField.getIsUsed())
            extensionParams.add(extensionField.getExtensionObject());
        }


        if (selfSigned.isSelected())
            certAndKey = certificateCreator.generateCertificateV2(x500NameBld.build(), extensionParams, new BigInteger(serialTF.getText()), dateFrom, dateTo);
        else {
            File ca = new File(caCertificateTF.getText());
            File caPkey = new File(caCertificatePkeyTF.getText());
            byte[] bytes = FileManager.read(ca);
            X509Certificate caCert = CertEnveloper.decodeCert(bytes);
            PrivateKey privateKey = CertEnveloper.decodePrivateKey(caPkey);
            certAndKey = certificateCreator.generateCertificateV2(x500NameBld.build(), extensionParams, new BigInteger(serialTF.getText()), dateFrom, dateTo, caCert, privateKey);
        }

        File output = new File("outputCerts/" + getThumbprint(certAndKey.getCertificate()));
        FileManager.write(new File(output.getAbsoluteFile() + ".cer"), CertEnveloper.encodeCert(certAndKey.getCertificate()));
        FileManager.write(new File(output.getAbsoluteFile() + ".pkey"), CertEnveloper.encodePrivateKey(certAndKey.getPrivateKey()));
        System.out.println("Записан сертификат " + output.getName());
    }
}
