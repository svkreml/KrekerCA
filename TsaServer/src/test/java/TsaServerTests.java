
import org.bouncycastle.jcajce.provider.digest.GOST3411;
import org.bouncycastle.tsp.TimeStampToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.UUID;

public class TsaServerTests {
/*    //@Test
    public static void main(String ... args) throws IOException, InterruptedException {
*//*        System.out.println("Запуск сервера");
       Thread thread =  new Thread(()->{
                    try {
                        CAserver.startServer(8082);
                        System.out.println("Сервер запущен");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }, "server");
       thread.start();
Thread.sleep(3000);*//*
        MessageDigest digest = new GOST3411.Digest();
        TsaClient tsaClient = new TsaClient(new URL("http://localhost:8082/tsa.srf"), "", "", digest);
       // TsaClient tsaClient = new TsaClient(new URL(" http://www.cryptopro.ru/tsp/tsp.srf"), "", "", digest);

        String originalMessage = UUID.randomUUID().toString();
        digest.digest(TsaClient.hexStringToByteArray(originalMessage));
        System.out.println("Отправка запроса");

        TimeStampToken token = tsaClient.getTimeStampToken(originalMessage.getBytes());
        tsaClient.printTokenInfo(token);
        final FileOutputStream output = new FileOutputStream("tspResponse.bin");
        IOUtils.write(token.getEncoded(), output);
        output.close();
        digest.reset();
        System.out.println("Совпадение хэша сообщения и хэша в штампе времени: "+tsaClient.validateTokenTimestamp(token, digest.digest(originalMessage.getBytes())));

    }*/
}
