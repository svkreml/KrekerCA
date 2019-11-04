package svkreml.krekerCa.core.hash;
import svkreml.krekerCa.Utils.MeUtils;
import org.bouncycastle.jcajce.provider.digest.GOST3411;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.logging.Logger;



@Deprecated
public class CreateHash {



    private static Logger logger = Logger.getLogger(CreateHash.class.getName());
    public static void main(String[] args) {
        if(args.length>1){
            for (String arg : args) {
                try {
                    digestGost2012_256(arg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static byte[] digestGost(byte[] bytes) {
        MessageDigest messageDigest = new GOST3411.Digest();
        byte[] hashedString = messageDigest.digest(bytes);
        String hex = MeUtils.bytesToHex(hashedString);
        logger.info("Получен хэш " + hex);
        return hashedString;
    }

    public static byte[] digestGost(File inputFile) throws IOException {
        logger.info("Генерация хэша ГОСТ " + inputFile);
        byte[] bytes = read(inputFile);
        return digestGost(bytes);
    }
    public static byte[] digestGost(String inputFile) throws IOException {
        return  digestGost(new File(inputFile));
    }

    //-----------------------2012_256
    public static byte[] digestGost2012_256(byte[] bytes) {
        MessageDigest messageDigest = new GOST3411.Digest2012_256();
        byte[] hashedString = messageDigest.digest(bytes);
        String hex = MeUtils.bytesToHex(hashedString);
        logger.info("Получен хэш " + hex);
        return hashedString;
    }
    public static byte[] digestGost2012_256(File inputFile) throws IOException {
        logger.info("Генерация хэша ГОСТ2012 256 " + inputFile);
        byte[] bytes = read(inputFile);
        return digestGost2012_256(bytes);
    }
    public static byte[] digestGost2012_256(String inputFile) throws IOException {
        return  digestGost2012_256(new File(inputFile));
    }




    //-----------------------2012_512
    public static byte[] digestGost2012_512(byte[] bytes) {
        MessageDigest messageDigest = new GOST3411.Digest2012_512();
        byte[] hashedString = messageDigest.digest(bytes);
        String hex = MeUtils.bytesToHex(hashedString);
        logger.info("Получен хэш " + hex);
        return hashedString;
    }
    public static byte[] digestGost2012_512(File inputFile) throws IOException {
        logger.info("Генерация хэша ГОСТ2012 512 " + inputFile);
        byte[] bytes = read(inputFile);
        return digestGost2012_512(bytes);
    }
    public static byte[] digestGost2012_512(String inputFile) throws IOException {
        return  digestGost2012_512(new File(inputFile));
    }


//custom
    public static byte[] digest(byte[] bytes, MessageDigest messageDigest) {
        byte[] hashedString = messageDigest.digest(bytes);
        String hex = MeUtils.bytesToHex(hashedString);
        logger.info("Получен хэш " + hex);
        return hashedString;
    }

    public static byte[] digest(File inputFile, MessageDigest messageDigest) throws IOException {
        logger.info("Генерация хэша ГОСТ " + inputFile);
        byte[] bytes = read(inputFile);
        return digest(bytes, messageDigest);
    }
    public static byte[] digest(String inputFile, MessageDigest messageDigest) throws IOException {
        return  digest(new File(inputFile), messageDigest);
    }






    //-----------------------спец методы
   private static byte[] read(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        int size = fis.available();
        byte[] bytes = new byte[size];
        if (fis.read(bytes) < 0)
            throw new IOException("input stream is empty");
        return bytes;
    }
}
