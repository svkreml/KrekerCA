import java.io.*;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Calendar;
import java.util.GregorianCalendar;

import caJava.fileManagement.CertEnveloper;
import caJava.fileManagement.FileManager;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.Req;
import org.bouncycastle.cert.ocsp.BasicOCSPRespBuilder;
import org.bouncycastle.cert.ocsp.RespID;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPRespBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

public class OcspServerTest implements HttpHandler {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            OcspServerTest ocspServerTest = new OcspServerTest();
            ocspServerTest.setupCA();
            
            // run HTTP server on port 16000 
            HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
            server.createContext("/ocsp", ocspServerTest);
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch ( Exception e ) {
            System.out.println("Http exception: " + e.getMessage());
        }
    }    boolean bRequireRequestSignature = true;    boolean bRequireNonce = true;
    
    public void handle(HttpExchange t) throws IOException {
         InputStream request = t.getRequestBody();
         byte[] requestBytes = new byte[10000];
         int requestSize = request.read(requestBytes);
         System.out.println("Received OCSP request, size: " + requestSize);
        
         byte[] responseBytes = new byte[2];
         responseBytes = processOcspRequest(requestBytes);
        
         Headers rh = t.getResponseHeaders();
         rh.set("Content-Type", "application/ocsp-response");
         t.sendResponseHeaders(200, responseBytes.length);
        
         OutputStream os = t.getResponseBody();
         os.write(responseBytes);
         os.close();
     }
     
     X509CertificateHolder internalCaCertificate = null;
     PrivateKey internalCaPrivateKey = null;
     
     private void setupCA() throws IOException {
         // initialize BouncyCastle
         Security.addProvider(new BouncyCastleProvider());
        
         byte[] bytes = null;
         try {
             File ca = new File("C:\\Users\\svkre\\IdeaProjects\\caJava\\Server\\OcspServer\\Ca.der");
             bytes = FileManager.read(ca);
             //caCert = CertEnveloper.decodeCert(bytes);
         } catch ( Exception e ) {
             e.printStackTrace();
             System.out.println("Cannot load Internal CA certificate file: " + e.getMessage());
             return;
         }
       
         try {
             internalCaCertificate = new X509CertificateHolder(CertEnveloper.decodeCert(bytes).getEncoded());
         } catch ( Exception e ) {
             e.printStackTrace();
             System.out.println("Cannot parse Internal CA certificate: " + e.getMessage());
         }
         File caPkey = new File("Server/OcspServer/Ca.der.pkey");
         internalCaPrivateKey = CertEnveloper.decodePrivateKey(caPkey);
     }
     
/*     private PrivateKey readPrivateKey(String fileName) {
         try {
             RandomAccessFile raf = new RandomAccessFile(fileName, "r");
             byte[] buf = new byte[(int)raf.length()];
             raf.readFully(buf);
             raf.close();
             PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(buf);
             KeyFactory kf = KeyFactory.getInstance("RSA");
             PrivateKey privKey = kf.generatePrivate(kspec);
             return privKey;
         } catch ( Exception e ) {
             System.out.println("Cannot load private key: " + e.getMessage());
             return null;
         }
     }*/
     
     public byte[] processOcspRequest(byte[] requestBytes) {
         try {
             // get request info
             OCSPReq ocspRequest = new OCSPReq(requestBytes);
             X509CertificateHolder[] requestCerts = ocspRequest.getCerts();
             Req[] requestList = ocspRequest.getRequestList();
            
             // setup response
             BasicOCSPRespBuilder basicOCSPRespBuilder = new BasicOCSPRespBuilder(new RespID(internalCaCertificate.getSubject()));
            
             System.out.println("OCSP request version: " + ocspRequest.getVersionNumber() + ", Requestor name: " + ocspRequest.getRequestorName() 
                         + ", is signed: " + ocspRequest.isSigned() + ", has extentions: " + ocspRequest.hasExtensions() 
                         + ", number of additional certificates: " + requestCerts.length + ", number of certificate ids to verify: " + requestList.length);
            
             int ocspResult = OCSPRespBuilder.SUCCESSFUL;
            
            /* // check request signature
             if ( ocspRequest.isSigned() )
             {
                 System.out.println("OCSP Request verify request signature: try certificates from request");
                
                 boolean bRequestSignatureValid = false;
                 for ( X509CertificateHolder cert : ocspRequest.getCerts() )
                 {
                     ContentVerifierProvider cpv = new JcaContentVerifierProviderBuilder().setProvider("BC").build(cert);
                     bRequestSignatureValid = ocspRequest.isSignatureValid(cpv);
                    
                     if ( bRequestSignatureValid )
                     {
                         break;
                     }
                 }
                
                 if ( !bRequestSignatureValid )
                 {
                     System.out.println("OCSP Request verify request signature: try CA certificate");
                     ContentVerifierProvider cpv = new JcaContentVerifierProviderBuilder().setProvider("BC").build(internalCaCertificate);
                     bRequestSignatureValid = ocspRequest.isSignatureValid(cpv);
                 }
                
                 if ( bRequestSignatureValid )
                 {
                     System.out.println("OCSP Request signature validation successful");
                 }
                 else
                 {
                     System.out.println("OCSP Request signature validation failed");
                     ocspResult = OCSPRespBuilder.UNAUTHORIZED;
                 }
             }
             else
             {
                 if ( bRequireRequestSignature )
                 {
                     System.out.println("OCSP Request signature is not present but required, fail the request");
                     ocspResult = OCSPRespBuilder.SIG_REQUIRED;
                 }
             }*/
            
             // process nonce
             Extension extNonce = ocspRequest.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
             if ( extNonce != null )
             {
                 System.out.println("Nonce is present in the request");
                 basicOCSPRespBuilder.setResponseExtensions(new Extensions(extNonce));
             }
             else
             {
                 System.out.println("Nonce is not present in the request");
                 if ( bRequireNonce )
                 {
                     System.out.println("Nonce is required, fail the request");
                     ocspResult = OCSPRespBuilder.UNAUTHORIZED;
                 }
             }
            
             // check all certificate serial numbers
             if ( ocspResult == OCSPRespBuilder.SUCCESSFUL )
             {
                 for ( Req req : requestList ) {
                     CertificateID certId = req.getCertID();
                     String serialNumber = "0x" + certId.getSerialNumber().toString(16);
                     CertificateStatus certificateStatus = null;

                     // check certId issuer/public key hash
                     System.out.println("Check issuer for certificate entry serial number: " + serialNumber);
                     if ( certId.matchesIssuer(internalCaCertificate, new BcDigestCalculatorProvider()) )
                     {
                         System.out.println("Check issuer successful");
                     }
                     else
                     {
                         System.out.println("Check issuer failed. Status unknown");
                         certificateStatus = new UnknownStatus();
                     }
                    
                     if ( certificateStatus == null ) 
                     {
                         System.out.println("Check revocation status for certificate entry serial number: " + serialNumber);
                        
                    /*     if ( serialNumber.equals("0x100001") ) {
                             certificateStatus = CertificateStatus.GOOD;
                             System.out.println("Status good");
                         } else*/
                         if ( serialNumber.equals("0x100002") ) {
                             System.out.println("Status revoked");
                             Calendar cal = new GregorianCalendar();
                             cal.set(2013,12,1);
                             certificateStatus = new RevokedStatus(cal.getTime(), 16);
                         } else {
                             System.out.println("Status unknown");
                             certificateStatus = CertificateStatus.GOOD;
                         }
                     }
                    
                     Calendar thisUpdate = new GregorianCalendar();
                     thisUpdate.set(2013,12,1);
                    
                     Calendar nextUpdate = new GregorianCalendar();
                     nextUpdate.set(2014,2,1);
                    
                     basicOCSPRespBuilder.addResponse(certId, certificateStatus, thisUpdate.getTime(), nextUpdate.getTime(), null);
                 }
             }
            
             X509CertificateHolder[] chain = {internalCaCertificate};
             ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(internalCaPrivateKey);



             BasicOCSPResp basicOCSPResp = basicOCSPRespBuilder.build(signer, chain, Calendar.getInstance().getTime() );
            
             OCSPRespBuilder ocspResponseBuilder = new OCSPRespBuilder();
             byte[] encoded = ocspResponseBuilder.build(ocspResult, basicOCSPResp).getEncoded();

             System.out.println("Sending OCSP response to client, size: " + encoded.length);
             return encoded;
            
         } catch ( Exception e ) {
             System.out.println("Exception during processing OCSP request: " + e.getMessage());
         }
        
         return null;
     }
}