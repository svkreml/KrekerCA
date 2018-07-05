package caJava.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class BcInit {  //BcInit.init();
    static public void init(){
        if(Security.getProvider("BC")==null)
            Security.addProvider(new BouncyCastleProvider());
    }
}
