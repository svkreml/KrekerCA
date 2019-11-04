package svkreml.krekerCa.core;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class BcInit {

    private static final String ENGINE_CLASS_PATH = "svkreml.krekerCa.core.engines.";

    static public void init() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
            addSignatureAlgorithm(((ConfigurableProvider) Security.getProvider("BC")),
                    "GOST3411", "ECGOST3410-HASH",
                    ENGINE_CLASS_PATH + "SignatureSpiHash"); // Добавляем алгориты для создания и проверки подписи по хэшу, а не только по исходным данным
            addSignatureAlgorithm(((ConfigurableProvider) Security.getProvider("BC")),
                    "GOST3411-2012-256", "ECGOST3410-2012-256-HASH",
                    ENGINE_CLASS_PATH + "ECGOST2012SignatureSpi256Hash");
            addSignatureAlgorithm(((ConfigurableProvider) Security.getProvider("BC")),
                    "GOST3411-2012-512", "ECGOST3410-2012-512-HASH",
                    ENGINE_CLASS_PATH + "ECGOST2012SignatureSpi512Hash");
        }
        assert Security.getProvider("BC").getInfo().equals(
                "BouncyCastle Security Provider v1.60");
    }


    private static void addSignatureAlgorithm(
            ConfigurableProvider provider,
            String digest,
            String algorithm,
            String className) {
        String mainName = digest + "WITH" + algorithm;
        String alias = digest + "/" + algorithm;

        provider.addAlgorithm("Signature." + mainName, className);
        provider.addAlgorithm("Alg.Alias.Signature." + alias, mainName);
    }
}
