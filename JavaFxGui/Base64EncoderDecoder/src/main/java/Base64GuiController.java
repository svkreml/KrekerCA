import caJava.fileManagement.FileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import org.bouncycastle.util.encoders.Base64;

import java.io.File;
import java.io.IOException;

public class Base64GuiController {
    final Clipboard clipboard = Clipboard.getSystemClipboard();


    @FXML
    public TextArea textAreaBase64;
    @FXML
    public TextArea textAreaOriginal;
    public byte[] textAreaOriginalBytes = null;
    @FXML
    public Button buttonCancel;
    public Button buttonCancel2;
    boolean bigFile = false;
    byte[] bigOriginalContent = null;
    String bigBase64Content = null;
    boolean isAlredyChanging = false;

    void setBigfile() {
        isAlredyChanging = true;
        if(bigOriginalContent!=null)
            bigBase64Content= Base64.toBase64String(bigOriginalContent);
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

    void unSetBigfile() {
        textAreaOriginal.setEditable(true);
        textAreaBase64.setEditable(true);
        bigBase64Content = null;
        textAreaOriginalBytes = null;
        bigFile = false;
        isAlredyChanging = false;
        buttonCancel.setVisible(false);
        buttonCancel2.setVisible(false);
        textAreaOriginal.clear();
        textAreaBase64.clear();
    }

    @FXML
    public void buttonLoadFileOriginal(ActionEvent actionEvent) {
        try {
            File file = JavaFxUtils.fileChooser("Выбор файла original");
            byte[] bytes = FileManager.read(file);
            if (bytes.length > JavaFxUtils.MAX_FILE_SIZE_FOR_TEXT_AREA) {
                bigOriginalContent = bytes;
                setBigfile();
            } else {
                unSetBigfile();
                textAreaOriginal.setText(new String(bytes, "UTF-8"));
            }
        } catch (NullPointerException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonSaveFileOriginal(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (bigFile) {
                FileManager.write(file, bigOriginalContent);
                unSetBigfile();
            } else
                FileManager.write(file, textAreaOriginal.getText().getBytes());
        }
    }

    @FXML
    public void buttonSaveFileBase64(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("base64 files (*.base64)", "*.base64");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (bigFile) {
                FileManager.write(file, bigBase64Content.getBytes());
                unSetBigfile();
            }
            else
                FileManager.write(file, textAreaBase64.getText().getBytes());
        }
    }

    @FXML
    public void buttonSaveToClipBoardOriginal(ActionEvent actionEvent) {
        final ClipboardContent content = new ClipboardContent();
        content.putString(textAreaOriginal.getText());
        clipboard.setContent(content);
    }

    @FXML
    public void buttonLoadFileBase64(ActionEvent actionEvent) {
        try {

            File file = JavaFxUtils.fileChooser("Выбор файла base64");
            byte[] bytes = FileManager.read(file);
            if (bytes.length > JavaFxUtils.MAX_FILE_SIZE_FOR_TEXT_AREA) {
                bigBase64Content = new String(bytes);
                setBigfile();
            } else {
                unSetBigfile();
                textAreaBase64.setText(new String(bytes));
            }

        } catch (NullPointerException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonSaveToClipBoardBase64(ActionEvent actionEvent) {
        final ClipboardContent content = new ClipboardContent();
        content.putString(textAreaBase64.getText());
        clipboard.setContent(content);
    }

    public void base64ToOriginal() {
        String textAreaBase64Text = textAreaBase64.getText();
        textAreaOriginal.setText(new String(Base64.decode(textAreaBase64Text)));
    }

    public void originalToBase64() {
        String textAreaOriginalText = textAreaOriginal.getText();
        textAreaBase64.setText(Base64.toBase64String(textAreaOriginalText.getBytes()));
    }


    @FXML
    public void initialize() {

        // Listen for changes in the text
        textAreaBase64.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                if (!isAlredyChanging) {
                    isAlredyChanging = true;
                    try {
                        base64ToOriginal();
                    } catch (Exception e) {
                        textAreaOriginal.setText("Не Base64");
                    }
                    isAlredyChanging = false;
                }
            }
        });
        textAreaOriginal.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                if (!isAlredyChanging) {
                    isAlredyChanging = true;
                    try {
                        originalToBase64();
                    } catch (Exception e) {
                    }
                    isAlredyChanging = false;
                }
            }
        });
    }

    public void buttonCancel(ActionEvent actionEvent) {
        unSetBigfile();
    }
}
