import caJava.Utils.MeUtils;
import caJava.core.CertAndKey;
import caJava.core.hash.CreateHash;
import caJava.core.pfx.PfxUtils;
import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.encoders.Base64;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CmsTests {

    @Test
    public void generateAll() throws IOException, SignatureException, CertificateException, CMSException, OperatorCreationException {
        File pfx = new File("..\\ConsoleApp\\exampleKeys\\gost2001Ca.pfx");
        System.out.println(pfx.getAbsolutePath());
        CertAndKey certAndKey = PfxUtils.convertToCertAndKey(pfx, "123456");
        byte[] originalContent = FileManager.read(new File("../Signer\\src\\test\\java\\CmsTests.java"));
        CMSSignedData cmsSignedDataDetached = Cms.generateEnvelopedSignature(originalContent, certAndKey, true);
        Cms.verifyDetached(cmsSignedDataDetached.getEncoded(), originalContent);
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataDetached.bin"), cmsSignedDataDetached.getEncoded());
        CMSSignedData cmsSignedDataAttached = Cms.generateEnvelopedSignature(originalContent, certAndKey, false);
        Cms.verifyAttached(cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.bin"), cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.base64"), Base64.encode(cmsSignedDataAttached.getEncoded()));
        byte[] digestGost = CreateHash.digestGost(originalContent);
        Cms.verifyByHash(cmsSignedDataDetached.getEncoded(), digestGost);
        FileManager.writeWithDir(new File("testSigner/digestGost.bin"), digestGost);
    }

    @Test
    public void signGost2012() throws Exception {
        MeUtils.loadBC();
        File cert = new File("t/t.cer");
        X509Certificate x509Certificate = CertEnveloper.decodeCert(FileManager.read(cert));
        File pkey = new File("t/test.pkey");
        PrivateKey privateKey = CertEnveloper.decodePrivateKey(pkey);
        CertAndKey certAndKey = new CertAndKey(privateKey, x509Certificate);

        byte[] originalContent = FileManager.read(new File("t/source.txt"));

        CMSSignedData cmsSignedDataAttached = Cms.generateEnvelopedSignature(originalContent, certAndKey, false);

        Cms.verifyAttached(cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.bin"), cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.base64"), Base64.encode(cmsSignedDataAttached.getEncoded()));
    }
    @Test
    public void signGost2012Pfx() throws Exception {
        MeUtils.loadBC();
        File pfx = new File("t\\test.pfx");
        System.out.println(pfx.getAbsolutePath());
        CertAndKey certAndKey = PfxUtils.convertToCertAndKey(pfx, "123456");

        byte[] originalContent = FileManager.read(new File("t/source.txt"));

        CMSSignedData cmsSignedDataAttached = Cms.generateEnvelopedSignature(originalContent, certAndKey, false);

        Cms.verifyAttached(cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.bin"), cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.base64"), Base64.encode(cmsSignedDataAttached.getEncoded()));
    }
    @Test
    public void signGost2001Pfx() throws Exception {
        MeUtils.loadBC();
        File pfx = new File("../ConsoleApp/exampleKeys/gost2001Ca.pfx");
        System.out.println(pfx.getAbsolutePath());
        CertAndKey certAndKey = PfxUtils.convertToCertAndKey(pfx, "123456");

        byte[] originalContent = FileManager.read(new File("t/source.txt"));

        System.out.println("rawSignature = " + new String(Base64.encode(CreateHash.digestGost(originalContent))));
        CMSSignedData cmsSignedDataAttached = Cms.generateEnvelopedSignature(originalContent, certAndKey, true);

        //Cms.verifyDetached(cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.bin"), cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.base64"), Base64.encode(cmsSignedDataAttached.getEncoded()));
    }
}
