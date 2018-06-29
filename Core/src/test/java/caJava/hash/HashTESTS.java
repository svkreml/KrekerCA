package caJava.hash;

import caJava.core.hash.CreateHash;
import caJava.fileManagement.FileManager;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class HashTESTS {
    @Test
    public void test() throws IOException {

       // CreateHash.digestGost2012_256("C:\\Users\\s.kremlev\\Desktop\\Тестирование ГУЦ 2017\\гост2012с\\CMS\\CMS\\gost2012strong_detach.sig");

        System.out.println(org.bouncycastle.util.encoders.Base64.toBase64String(CreateHash.digestGost2012_256("C:\\Users\\s.kremlev\\Desktop\\guts\\guts test data 16.04.18\\вложения\\2001\\file.pdf")));

    }//BAA8FE1446A7B7708D3A82BFA881DB925F57EE8640601359BAE8375AAC9D7E76
}
