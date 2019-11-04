package svkreml.krekerCa.core.hash;

import org.bouncycastle.jcajce.provider.digest.GOST3411;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class DigestGost {
    final private static int PACKET_SIZE = 4096;


    public static byte[] gost(byte[] bytes) {
        MessageDigest messageDigest = new GOST3411.Digest();
        return messageDigest.digest(bytes);
    }

    public static byte[] gost2012_256(byte[] bytes) {
        MessageDigest messageDigest = new GOST3411.Digest2012_256();
        return messageDigest.digest(bytes);
    }

    public static byte[] gost2012_512(byte[] bytes) {
        MessageDigest messageDigest = new GOST3411.Digest2012_512();
        return messageDigest.digest(bytes);
    }


    public static byte[] gost(InputStream inputStream) throws IOException {
        MessageDigest messageDigest = new GOST3411.Digest();
        int length;
        byte[] data = new byte[PACKET_SIZE];
        while ((length = inputStream.read(data, 0, data.length)) != -1) {
            messageDigest.update(data, 0, length);
        }
        return messageDigest.digest();
    }

    public static byte[] gost2012_256(InputStream inputStream) throws IOException {
        MessageDigest messageDigest = new GOST3411.Digest2012_256();
        int length;
        byte[] data = new byte[PACKET_SIZE];
        while ((length = inputStream.read(data, 0, data.length)) != -1) {
            messageDigest.update(data, 0, length);
        }
        return messageDigest.digest();
    }

    public static byte[] gost2012_512(InputStream inputStream) throws IOException {
        MessageDigest messageDigest = new GOST3411.Digest2012_512();
        int length;
        byte[] data = new byte[PACKET_SIZE];
        while ((length = inputStream.read(data, 0, data.length)) != -1) {
            messageDigest.update(data, 0, length);
        }
        return messageDigest.digest();
    }
}
