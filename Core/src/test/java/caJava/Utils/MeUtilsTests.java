package caJava.Utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MeUtilsTests {
    static byte[] printArray(byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(b + " ");
        }
        System.out.println();
        return bytes;
    }

    @Test
    public void test1() {
        byte[] bytes1 = {1, 2, 3};
        byte[] bytes2 = {3, 4, 5};
        byte[] bytes3 = {4, 5, 6};
        byte[] bytes4 = {4, 5, 6};
        Assert.assertEquals(MeUtils.concatBytes(bytes1, bytes2, bytes3, bytes4), new byte[]{1, 2, 3, 3, 4, 5, 4, 5, 6, 4, 5, 6});
        Assert.assertEquals(MeUtils.concatBytes(bytes1, bytes2), new byte[]{1, 2, 3, 3, 4, 5});
        Assert.assertEquals(MeUtils.concatBytes(bytes1), new byte[]{1, 2, 3});
        Assert.assertEquals(MeUtils.concatBytes(), new byte[]{});
    }
}
