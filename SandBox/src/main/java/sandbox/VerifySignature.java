package sandbox;

import java.io.File;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

public class VerifySignature {
    public static void main(String[] args) throws Exception {
        File p7s = new File("D:\\SBI-DATA\\file\\signature_2.txt") ;
        int size = ((int) p7s.length());
        byte[] sig = new byte[size]; 
        File f = new File("D:\\SBI-DATA\\file\\plain.txt") ;
        int sizecontent = ((int) f.length());
        byte[] Data_Bytes = new byte[sizecontent];  
        Security.addProvider(new BouncyCastleProvider());    
        CMSSignedData signedData = new CMSSignedData(new CMSProcessableByteArray(Data_Bytes), sig);
            Store store = signedData.getCertificates(); 
            SignerInformationStore signers = signedData.getSignerInfos(); 
            Collection c = signers.getSigners(); 
            Iterator it = c.iterator();
            while (it.hasNext()) { 
                SignerInformation signer = (SignerInformation) it.next(); 
                Collection certCollection = store.getMatches(signer.getSID()); 
                Iterator certIt = certCollection.iterator();
                X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
                X509Certificate certFromSignedData = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(certFromSignedData))) {
                    System.out.println("Signature verified");
                } else {
                    System.out.println("Signature verification failed");
                }
            }
    }    
}  