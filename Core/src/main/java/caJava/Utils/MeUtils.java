package caJava.Utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.logging.Logger;
/*
* методы исполльзуемые по всему модулю
* */
public class MeUtils {
    private static Logger logger = Logger.getLogger(MeUtils.class.getName());
    public static byte[] concatBytes(byte[] ... bytesArray) {
        int size=0;
        for (byte[] bytes : bytesArray) {
            size+=bytes.length;
        }
        byte[] result = new byte[size];
        int pos=0;
        for (byte[] bytes : bytesArray) {
            System.arraycopy(bytes, 0, result, pos, bytes.length);
            pos+=bytes.length;
        }
        return result;
    }
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();


    public static void loadBC(){
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
            logger.info("Криптопровайдер BC был загружен");
        }
    }
}
