import org.apache.commons.io.IOUtils;
import org.bouncycastle.jcajce.provider.digest.GOST3411;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.tsp.TimeStampToken;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.UUID;


public class TsaClientTests {


    @Test
    public void getSha256TimeStamp() throws IOException {
        MessageDigest digest = new SHA256.Digest();
        TsaClient tsaClient = new TsaClient(new URL("http://testguc/TSP/tsp.srf"), "", "", digest);

        String originalMessage = UUID.randomUUID().toString();
        digest.digest(TsaClient.hexStringToByteArray(originalMessage));

        TimeStampToken token = tsaClient.getTimeStampToken(originalMessage.getBytes());

        tsaClient.printTokenInfo(token);
        final FileOutputStream output = new FileOutputStream("tspResponse.bin");
        IOUtils.write(token.getEncoded(), output);
        output.close();
        digest.reset();
        System.out.println("Совпадение хэша сообщения и хэша в штампе времени: "+tsaClient.validateTokenTimestamp(token, digest.digest(originalMessage.getBytes())));
    }
    @Test      //MessageDigest digest = new GOST3411.Digest2012_256();
    public void getGost2001TimeStamp() throws IOException {
        MessageDigest digest = new GOST3411.Digest();
        TsaClient tsaClient = new TsaClient(new URL("http://localhost:8082/tsa.srf"), "", "", digest);

        String originalMessage = UUID.randomUUID().toString();
        digest.digest(TsaClient.hexStringToByteArray(originalMessage));

        TimeStampToken token = tsaClient.getTimeStampToken(originalMessage.getBytes());

        tsaClient.printTokenInfo(token);
        final FileOutputStream output = new FileOutputStream("tspResponse.bin");
        IOUtils.write(token.getEncoded(), output);
        output.close();
        digest.reset();
        System.out.println("Совпадение хэша сообщения и хэша в штампе времени: "+tsaClient.validateTokenTimestamp(token, digest.digest(originalMessage.getBytes())));
    }

    @Test
    public void getGost2012_256TimeStamp() throws IOException {
        MessageDigest digest = new GOST3411.Digest2012_256();
        //TsaClient tsaClient = new TsaClient(new URL("http://testguc/TSP/tsp.srf"), "", "", digest);
        TsaClient tsaClient = new TsaClient(new URL("http://www.cryptopro.ru/tsp/tsp.srf"), "", "", digest);

        String originalMessage = UUID.randomUUID().toString();
        digest.digest(TsaClient.hexStringToByteArray(originalMessage));

        TimeStampToken token = tsaClient.getTimeStampToken(originalMessage.getBytes());

        tsaClient.printTokenInfo(token);
        final FileOutputStream output = new FileOutputStream("tspResponse.bin");
        IOUtils.write(token.getEncoded(), output);
        output.close();
        digest.reset();
        System.out.println("Совпадение хэша сообщения и хэша в штампе времени: "+tsaClient.validateTokenTimestamp(token, digest.digest(originalMessage.getBytes())));
    }
}