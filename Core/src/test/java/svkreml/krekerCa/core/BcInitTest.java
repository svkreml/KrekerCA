package svkreml.krekerCa.core;


import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.Security;

public class BcInitTest {
    @Test
    public void bcInit(){
        BcInit.init();


        Assert.assertEquals(Security.getProvider("BC").getInfo(),
                "BouncyCastle Security Provider v1.60",
                "В данный момент ожидается именно эта версия, в остальных может быть ошибка");
    }
}
