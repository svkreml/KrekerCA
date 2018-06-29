package sandbox;

import caJava.fileManagement.FileManager;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

        /*
         * https://ru.stackoverflow.com/questions/507393/%D0%A8%D0%B8%D1%84%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-%D0%BF%D0%BE-%D0%93%D0%9E%D0%A1%D0%A2-28147-89-%D0%B2-bouncycastle-java
         * */

public class Crypt {
    public static void main(String[] args) throws IOException {
        Random random = new Random();

        byte[] key1 = new byte[32];
        byte[] key2 = new byte[32];
        byte[] key3 = new byte[32];
        random.nextBytes(key1);
        byte[] inBytes = FileManager.read(new File("SandBox\\src\\main\\java\\sandbox\\Crypt.java"));

        byte[] outBytes = crypt(key1, inBytes, true);
        //--------------------------------------------------------------------
        outBytes = crypt(key3, outBytes, false);


        String decodedString = new String(outBytes, Charset.defaultCharset());
        System.out.println(decodedString);
    }

    private static byte[] crypt(byte[] key, byte[] inBytes, boolean ende) {
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(
                new GOST28147Engine()));
        cipher.init(ende, new KeyParameter(key));
        byte[] outBytes = new byte[cipher.getOutputSize(inBytes.length)];
        int len = cipher.processBytes(inBytes, 0, inBytes.length, outBytes, 0);
        try {
            cipher.doFinal(outBytes, len);
        } catch (CryptoException e) {
            System.out.println("Exception: " + e.toString());
        }
        return outBytes;
    }

}