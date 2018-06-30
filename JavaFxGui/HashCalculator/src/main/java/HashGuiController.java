import caJava.core.hash.CreateHash;
import caJava.fileManagement.FileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;


public class HashGuiController {
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    @FXML
    public TextArea textAreaOrig;
    @FXML
    public AnchorPane radioButtonAnchorPane;
    @FXML
    public TextArea textAreaHash;
    private byte[] originalContent = null;


    @FXML
    public void buttonSaveToClipBoard(ActionEvent actionEvent) {
        final ClipboardContent content = new ClipboardContent();
        content.putString(textAreaHash.getText());
        clipboard.setContent(content);
    }

    @FXML
    public void buttonLoadFile(ActionEvent actionEvent) {
        try {
            File file = JavaFxUtils.fileChooser("Выбор файла");
            byte[] bytes = FileManager.read(file);
            originalContent = bytes;
            if (bytes.length > JavaFxUtils.MAX_FILE_SIZE_FOR_TEXT_AREA) {
                textAreaOrig.setText("До изменения этого поля в Хэше отображается файл");
            } else {
                textAreaOrig.setText(new String(bytes, "UTF-8"));
            }
        } catch (NullPointerException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        // Radio 1: Male

        RadioButton button1 = new RadioButton("Male");
        button1.setToggleGroup(group);
        button1.setSelected(true);
        RadioButton button2 = new RadioButton("Female");
        button2.setToggleGroup(group);

        HBox root = new HBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);
        root.getChildren().addAll(button1, button2);
        radioButtonAnchorPane.getChildren().add(root);

        textAreaOrig.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                try {
                    originalContent = null;
                    digest();
                } catch (Exception e) {
                }
            }
        });
    }

    private void digest() {
        if(originalContent!=null){
            byte[] bytes = CreateHash.digestGost(originalContent);
        }else{

        }

    }

}
