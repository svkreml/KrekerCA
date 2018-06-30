import javafx.stage.FileChooser;

import java.io.File;

public class JavaFxUtils {
    final public static int MAX_FILE_SIZE_FOR_TEXT_AREA = 1_000_000;

    public static File fileChooser(String s) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        fileChooser.setTitle(s);
        fileChooser.setInitialDirectory(new File("C:\\"));
        if (selectedFile != null) {
            return selectedFile;
        }
        throw new NullPointerException("Файл не выбран");
    }
}
