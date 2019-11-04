package caJava.hash;

import svkreml.krekerCa.core.hash.CreateHash;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class HashTESTS {
    @Test
    public void test() throws IOException {


        String input ="0";
        System.out.println(org.bouncycastle.util.encoders.Base64.toBase64String(CreateHash.digestGost(input.getBytes())));


    }
}
