package svkreml.krekerCa.fileManagement;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.logging.Logger;
/*
* маленький класс для работы с файлами
* */
@Slf4j
public class FileManager {
    public static byte[] read(File file) throws IOException {
        log.info("чтение файла "+ file.getAbsolutePath());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found: "+ file.getAbsolutePath());
        }
        int size = fis.available();
        byte[] bytes = new byte[size];
        if (fis.read(bytes) < 0)
            throw new IOException("input stream is empty");
        log.info("файл прочитан, "+ bytes.length +" байт");
        return bytes;
    }
    public static void write(File file, byte[] bytes) throws IOException {
        log.info("запись в файл "+ file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.flush();
        fos.close();
        log.info("файл записан, "+ bytes.length +" байт");
    }
    public static void writeWithDir(File file, byte[] bytes) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if(!parent.exists())
            parent.mkdirs();
        log.info("запись в файл "+ file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.flush();
        fos.close();
        log.info("файл записан, "+ bytes.length +" байт");
    }
}
