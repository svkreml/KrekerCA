package caJava.fileManagement;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class FileManagerTests {
    //todo протестировать методы
    @Test
    public void test1() {
        byte[] bytes = {1,2,3,4,5};
        File file = new File("test.txt");

        try {
            if(file.createNewFile())
                System.out.println("не удалось создать "+ file.getAbsolutePath());;
            FileManager.write(file, bytes);
            byte[] read = FileManager.read(file);
            if(!file.delete()) //fixme файл почему-то не удаляется
                System.out.println("ошибка при удалении тестового файла");
            Assert.assertEquals(read,bytes,"Проверка записи и чтения");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
