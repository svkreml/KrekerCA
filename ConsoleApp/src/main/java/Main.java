import caJava.Utils.MeUtils;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.LinkedHashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        MeUtils.loadBC();
        LinkedHashMap<String, String> params = parseParams(args);
        System.out.println(params);
        switch (params.get("run")) {
            case "createCert":
                CreateCert.run(params);
                break;
            case "convert":
                Convert.run(params);
                break;
            default:
                System.out.println("нет такого параметра run");
        }
    }

    private static LinkedHashMap<String, String> parseParams(String[] args) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        String help = "Аругументы должны идти парами сначала параметр с '-' вначале, значения может не быть, всё внутри двойных кавычек считается за одно значение";
        if (args.length == 0)
            System.out.println(help);
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.charAt(0) == '-') {
                if (args[i + 1] != null && args[i + 1].charAt(0) != '-')
                    params.put(arg.substring(1), args[i + 1]);
                else
                    params.put(arg.substring(1), null);
            }
        }
        return params;
    }
}
