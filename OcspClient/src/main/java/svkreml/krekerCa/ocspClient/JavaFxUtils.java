package svkreml.krekerCa.ocspClient;

import svkreml.krekerCa.fileManagement.FileManager;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class JavaFxUtils {
    final public static int MAX_FILE_SIZE_FOR_TEXT_AREA = 1_000_000;
    static private File lastOpenedFile = null;
    static private File  propFile =new File("last.prop");
    static {
        try {
            lastOpenedFile = new File(new String(FileManager.read(propFile)));
            if(!lastOpenedFile.isDirectory()||!lastOpenedFile.exists())
                lastOpenedFile = new File("C:\\");
        } catch (IOException e) {
            lastOpenedFile = new File("C:\\");
        }
    }

    public static File fileChooser(String s) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(s);
        fileChooser.setInitialDirectory(lastOpenedFile);
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            if (!selectedFile.equals(lastOpenedFile)){
                try {
                    lastOpenedFile=selectedFile.getParentFile();
                    FileManager.write(propFile, lastOpenedFile.getAbsolutePath().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return selectedFile;
        }
        throw new NullPointerException("Файл не выбран");
    }
}
