import caJava.fileManagement.FileManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import org.bouncycastle.util.encoders.Base64;
import svkreml.krekerCa.gui.JavaFxUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Base64GuiController {
    private final Clipboard clipboard = Clipboard.getSystemClipboard();


    @FXML
    public TextArea textAreaBase64;
    @FXML
    public TextArea textAreaOriginal;
    @FXML
    public Button buttonCancel;
    public Button buttonCancel2;
    private boolean bigFile = false;
    private byte[] bigOriginalContent = null;
    private String bigBase64Content = null;
    private boolean isAlreadyChanging = false;

    private void setBigfile() {
        isAlreadyChanging = true;
        if (bigOriginalContent != null)
            bigBase64Content = Base64.toBase64String(bigOriginalContent);
        else
            try {
                bigOriginalContent = Base64.decode(bigBase64Content);
            } catch (Exception e) {
                textAreaOriginal.setText("Не Base64");
                return;
            }
        textAreaOriginal.setText("Крупный файл");
        textAreaBase64.setText("Крупный файл");
        textAreaOriginal.setEditable(false);
        textAreaBase64.setEditable(false);
        bigFile = true;
        buttonCancel.setVisible(true);
        buttonCancel2.setVisible(true);
    }

    private void unSetBigfile() {
        textAreaOriginal.setEditable(true);
        textAreaBase64.setEditable(true);
        bigBase64Content = null;
        bigFile = false;
        isAlreadyChanging = false;
        buttonCancel.setVisible(false);
        buttonCancel2.setVisible(false);
        textAreaOriginal.clear();
        textAreaBase64.clear();
    }

    @FXML
    public void buttonLoadFileOriginal() {
        try {
            File file = JavaFxUtils.fileChooser("Выбор файла original");
            byte[] bytes = FileManager.read(file);
            if (bytes.length > JavaFxUtils.MAX_FILE_SIZE_FOR_TEXT_AREA) {
                bigOriginalContent = bytes;
                setBigfile();
            } else {
                unSetBigfile();
                textAreaOriginal.setText(new String(bytes, "Cp1252"));
            }
        } catch (NullPointerException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonSaveFileOriginal() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (bigFile) {
                FileManager.write(file, bigOriginalContent);
                unSetBigfile();
            } else
                FileManager.write(file, textAreaOriginal.getText().getBytes("Cp1252"));
        }
    }

    @FXML
    public void buttonSaveFileBase64() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("base64 files (*.base64)", "*.base64");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (bigFile) {
                FileManager.write(file, bigBase64Content.getBytes("Cp1252"));
                unSetBigfile();
            } else
                FileManager.write(file, textAreaBase64.getText().getBytes("Cp1252"));
        }
    }

    @FXML
    public void buttonSaveToClipBoardOriginal() {
        final ClipboardContent content = new ClipboardContent();
        content.putString(textAreaOriginal.getText());
        clipboard.setContent(content);
    }

    @FXML
    public void buttonLoadFileBase64() throws NullPointerException {
        try {

            File file = JavaFxUtils.fileChooser("Выбор файла base64");
            byte[] bytes = FileManager.read(file);
            if (bytes.length > JavaFxUtils.MAX_FILE_SIZE_FOR_TEXT_AREA) {
                bigBase64Content = new String(bytes, "Cp1252");
                setBigfile();
            } else {
                unSetBigfile();
                textAreaBase64.setText(new String(bytes, "Cp1252"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonSaveToClipBoardBase64() {
        final ClipboardContent content = new ClipboardContent();
        content.putString(textAreaBase64.getText());
        clipboard.setContent(content);
    }

    private void base64ToOriginal() throws UnsupportedEncodingException {
        String textAreaBase64Text = textAreaBase64.getText();
        textAreaOriginal.setText(new String(Base64.decode(textAreaBase64Text), "Cp1252"));
    }

    private void originalToBase64() throws UnsupportedEncodingException {
        String textAreaOriginalText = textAreaOriginal.getText();
        textAreaBase64.setText(new String(Base64.encode(textAreaOriginalText.getBytes("Cp1252")), "Cp1252"));
    }


    @FXML
    public void initialize() {
        System.setProperty("file.encoding", "Cp1252");
        // Listen for changes in the text
        textAreaBase64.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isAlreadyChanging) {
                isAlreadyChanging = true;
                try {
                    base64ToOriginal();
                } catch (Exception e) {
                    textAreaOriginal.setText("Не Base64");
                }
                isAlreadyChanging = false;
            }
        });
        textAreaOriginal.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isAlreadyChanging) {
                isAlreadyChanging = true;
                try {
                    originalToBase64();
                } catch (Exception ignored) {
                }
                isAlreadyChanging = false;
            }
        });
    }

    public void buttonCancel() {
        unSetBigfile();
    }
}
