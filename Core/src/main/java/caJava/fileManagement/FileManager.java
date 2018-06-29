package caJava.fileManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
/*
* маленький класс для работы с файлами
* */
public class FileManager {
    static Logger logger = Logger.getLogger(FileManager.class.getName());
    public static byte[] read(File file) throws IOException {
        logger.info("чтение файла "+ file.getAbsolutePath());
        FileInputStream fis = new FileInputStream(file);
        int size = fis.available();
        byte[] bytes = new byte[size];
        if (fis.read(bytes) < 0)
            throw new IOException("input stream is empty");
        logger.info("файл прочитан, "+ bytes.length +" байт");
        return bytes;
    }
    public static void write(File file, byte[] bytes) throws IOException {
        logger.info("запись в файл "+ file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.flush();
        fos.close();
        logger.info("файл записан, "+ bytes.length +" байт");
    }
    public static void writeWithDir(File file, byte[] bytes) throws IOException {
        File parent = file.getParentFile();
        if(!parent.exists())
            parent.mkdirs();
        logger.info("запись в файл "+ file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.flush();
        fos.close();
        logger.info("файл записан, "+ bytes.length +" байт");

    }
}
