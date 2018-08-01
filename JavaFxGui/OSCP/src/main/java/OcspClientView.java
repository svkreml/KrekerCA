import caJava.fileManagement.FileManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.synapse.transport.certificatevalidation.ocsp.OCSPVerifier;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import sun.security.x509.SerialNumber;
import sun.security.x509.X509CertImpl;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.UUID;


public class OcspClientView {
    @FXML
    public TextField ocspServerTextField;
    @FXML
    public TextField caCertTextField;
    @FXML
    public TextField userCertTextField;
    @FXML
    public TextArea responseTextArea;
    @FXML
    public TextField crlServerTextField;
    String ocspTempLocation = null;

    private static X509Certificate getCertFromFile(String path) {
        X509Certificate cert = null;
        try {

            File certFile = new File(path);
            if (!certFile.canRead())
                throw new IOException(" File " + certFile.toString() +
                        " is unreadable");

            FileInputStream fis = new FileInputStream(path);
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            cert = (X509Certificate) cf.generateCertificate(fis);

        } catch (Exception e) {
            System.out.println("Can't construct X509 Certificate. " +
                    e.getMessage());
        }
        return cert;
    }

    @FXML
    public void fileChooseUserCert() throws Exception {
        userCertTextField.setText(JavaFxUtils.fileChooser("Выбор сертификата для проверки").getAbsolutePath());
        tryToOcspButton();
    }

    @FXML
    public void fileChooseCaCert() {
        caCertTextField.setText(JavaFxUtils.fileChooser("Выбор сертификата УЦ для проверки").getAbsolutePath());
    }

    private void fileChooser(String s, TextField caCertTextField) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        fileChooser.setTitle(s);
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Сертификаты", "*.cer", "*.der"));
        if (selectedFile != null) {
            caCertTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void tryToFindCaCertButton() throws Exception {
        KeyStore keystore;
        // Get instance of the keystore
        //keystore = KeyStore.getInstance("Windows-MY");
        keystore = KeyStore.getInstance("Windows-ROOT");
        // Loading the keystore
        keystore.load(null, null);

        X509Certificate userCert = getCertFromFile(userCertTextField.getText());
        SerialNumber serialNumberCa = (SerialNumber) ((X509CertImpl) userCert).getAuthorityKeyIdentifierExtension().get("serial_number");

        String certificateAlias = keystore.getCertificateAlias(userCert);
        Certificate[] certificateChain = keystore.getCertificateChain(certificateAlias);

        for (Enumeration oEnum = keystore.aliases();
             oEnum.hasMoreElements(); ) {
            String sAlias = (String) oEnum.nextElement();
            X509Certificate keystoreCertificate = (X509Certificate) keystore.getCertificate(sAlias);
            SerialNumber serialNumber = ((X509CertImpl) keystoreCertificate).getSerialNumberObject();
            if (serialNumberCa.getNumber().equals(serialNumber.getNumber())) {
                try {
                    userCert.verify(keystoreCertificate.getPublicKey());
                } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
                    continue;
                }
                caCertTextField.setText("temp/" + UUID.randomUUID().toString() + ".cer");
                File file = new File(caCertTextField.getText());
                FileManager.writeWithDir(file, keystoreCertificate.getEncoded());
                break;
            }

        }
    }

    @FXML
    public void tryToOcspButton() throws Exception {
        String location = OCSPVerifier.getOcspUrl(getCertFromFile(userCertTextField.getText()));
        ocspServerTextField.setText(location);
    }

    /**
     * An OCSP request is made to be given to the fake CA. Reflection is used to call generateOCSPRequest(..) private
     * method in OCSPVerifier.
     *
     * @param caCert              the fake CA certificate.
     * @param revokedSerialNumber the serial number of the certificate which needs to be checked if revoked.
     * @return the created OCSP request.
     * @throws Exception
     */
    private OCSPReq getOCSPRequest(X509Certificate caCert, BigInteger revokedSerialNumber) throws Exception {
        OCSPVerifier ocspVerifier = new OCSPVerifier(null);
        Class ocspVerifierClass = ocspVerifier.getClass();
        Method generateOCSPRequest = ocspVerifierClass.getDeclaredMethod("generateOCSPRequest", X509Certificate.class,
                BigInteger.class);
        generateOCSPRequest.setAccessible(true);

        OCSPReq request = (OCSPReq) generateOCSPRequest.invoke(ocspVerifier, caCert, revokedSerialNumber);
        return request;
    }

    @FXML
    public void runOcspButton() throws Exception {
        responseTextArea.clear();
        //Add BouncyCastle as Security Provider.
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        X509Certificate userCert = getCertFromFile(userCertTextField.getText());
        X509Certificate caCert = getCertFromFile(caCertTextField.getText());

        //Create OCSP request to check if certificate with "serialNumber == revokedSerialNumber" is revoked.
        X509CertificateHolder userCertHolder = new X509CertificateHolder(userCert.getEncoded());
        OCSPReq request = getOCSPRequest(caCert, userCertHolder.getSerialNumber());


        final byte[] array = request.getEncoded();

        HttpURLConnection con = null;
        if (ocspServerTextField.getText().length() < 5)
            tryToOcspButton();
        final URL url = new URL(ocspServerTextField.getText());
        con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/ocsp-request");
        con.setRequestProperty("Accept", "application/ocsp-response");
        con.setDoOutput(true);
        final OutputStream out = con.getOutputStream();
        final DataOutputStream dataOut = new DataOutputStream(
                new BufferedOutputStream(out));
        dataOut.write(array);

        dataOut.flush();
        dataOut.close();

        // Get Response
        final InputStream in = (InputStream) con.getContent();


        // Fetch the responses
        final OCSPResp ocspResponse = new OCSPResp(in);
        final BasicOCSPResp basicResponse = (BasicOCSPResp) ocspResponse
                .getResponseObject();
        if (basicResponse.getResponses().length < 1) {
            responseTextArea.setText("ERROR");
            return;
        }

        SingleResp singleResp = basicResponse.getResponses()[0];
        Object status = singleResp.getCertStatus();
        if (status == null)
            responseTextArea.setText("GOOD");
        else if (status instanceof UnknownStatus)
            responseTextArea.setText("UnknownStatus");
        else if (status instanceof RevokedStatus) {
            responseTextArea.setText("RevokedStatus\n" +
                    ((RevokedStatus) status).getRevocationTime() + "\n" +
                    CRLReason.lookup(((RevokedStatus) status).getRevocationReason()));
        }
        X509CertificateHolder x509CertificateHolder = basicResponse.getCerts()[0];

/*        X509Certificate x509Certificate = new JcaX509CertificateConverter().setProvider("BC")
                .getCertificate(x509CertificateHolder);
        responseTextArea.appendText("\n\n"+x509Certificate);*/

        ocspTempLocation = "temp/" + UUID.randomUUID().toString() + ".cer";
        File file = new File(ocspTempLocation);
        FileManager.writeWithDir(file, x509CertificateHolder.getEncoded());
    }

    @FXML
    public void showOcspCertButton() throws IOException, InterruptedException {
        Runtime runTime = Runtime.getRuntime();
        Process process = runTime.exec("\"C:\\Windows\\System32\\rundll32.exe\" cryptext.dll,CryptExtOpenCER " + ocspTempLocation);

    }


    @FXML
    public void tryToCrlButton(ActionEvent actionEvent) {

    }

    @FXML
    public void runCrlButton(ActionEvent actionEvent) {

    }
}
