import caJava.Utils.MeUtils;
import caJava.core.BcInit;
import caJava.core.CertAndKey;
import caJava.core.hash.CreateHash;
import caJava.core.pfx.PfxUtils;
import caJava.fileManagement.FileManager;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.encoders.Base64;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

public class CmsTests {

    @Test
    public void generateAll() throws IOException, SignatureException, CertificateException, CMSException, OperatorCreationException {
        File pfx = new File("..\\ConsoleApp\\exampleKeys\\gost2001Ca.pfx");
        System.out.println(pfx.getAbsolutePath());
        CertAndKey certAndKey = PfxUtils.convertToCertAndKey(pfx, "123456");
        byte[] originalContent = FileManager.read(new File("../Signer/testSigner/source.txt"));
        CMSSignedData cmsSignedDataDetached = Cms.generateEnvelopedSignature(originalContent, certAndKey, true);
        Cms.verifyDetached(cmsSignedDataDetached.getEncoded(),originalContent);
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataDetached.bin"), cmsSignedDataDetached.getEncoded());
        CMSSignedData cmsSignedDataAttached = Cms.generateEnvelopedSignature(originalContent, certAndKey, false);
        Cms.verifyAttached(cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.bin"), cmsSignedDataAttached.getEncoded());
        FileManager.writeWithDir(new File("testSigner/cmsSignedDataAttached.base64"), Base64.encode(cmsSignedDataAttached.getEncoded()));
        byte[] digestGost = CreateHash.digestGost(originalContent);
        Cms.verifyByHash(cmsSignedDataDetached.getEncoded(), digestGost);
        FileManager.writeWithDir(new File("testSigner/digestGost.bin"), digestGost);
    }
}
