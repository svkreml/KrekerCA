/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.synapse.transport.certificatevalidation.ocsp;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.transport.certificatevalidation.CertificateVerificationException;
import org.apache.synapse.transport.certificatevalidation.RevocationStatus;
import org.apache.synapse.transport.certificatevalidation.RevocationVerifier;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.jcajce.provider.digest.GOST3411;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DigestOutputStream;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Used to check if a Certificate is revoked or not by its CA using Online Certificate Status Protocol (OCSP).
 */
public class OCSPVerifier implements RevocationVerifier {

    private OCSPCache cache;
    private static final Log log = LogFactory.getLog(OCSPVerifier.class);

    public OCSPVerifier(OCSPCache cache) {
        this.cache = cache;
    }

    /**
     * Gets the revocation status (Good, Revoked or Unknown) of the given peer certificate.
     *
     * @param peerCert   The certificate we want to check if revoked.
     * @param issuerCert Needed to create OCSP request.
     * @return revocation status of the peer certificate.
     * @throws CertificateVerificationException
     *
     */
    public RevocationStatus checkRevocationStatus(X509Certificate peerCert, X509Certificate issuerCert)
            throws CertificateVerificationException {

        //check cache
        if (cache != null) {
           SingleResp resp = cache.getCacheValue(peerCert.getSerialNumber());
            if (resp != null) {
                //If cant be casted, we have used the wrong cache.
                RevocationStatus status = getRevocationStatus(resp);
                log.info("OCSP response taken from cache....");
                return status;
            }
        }

        OCSPReq request = generateOCSPRequest(issuerCert, peerCert.getSerialNumber());
        //This list will sometimes have non ocsp urls as well.
        List<String> locations = new ArrayList<String>();
        try {
            locations.add(getOcspUrl(peerCert));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String serviceUrl : locations) {

            SingleResp[] responses;
            try {
                OCSPResp ocspResponse = getOCSPResponce(serviceUrl, request);
                if (OCSPResp.SUCCESSFUL != ocspResponse.getStatus()) {
                    continue; // Server didn't give the response right.
                }

                BasicOCSPResp basicResponse = (BasicOCSPResp) ocspResponse.getResponseObject();
                responses = (basicResponse == null) ? null : basicResponse.getResponses();
                log.info("OSCP url: "+serviceUrl);

                //todo use the super exception
            } catch (Exception e) {
                continue;
            }

            if (responses != null && responses.length == 1) {
                SingleResp resp = responses[0];
                RevocationStatus status = getRevocationStatus(resp);
                if (cache != null)
                    cache.setCacheValue(peerCert.getSerialNumber(), resp, request, serviceUrl);
                return status;
            }
        }
        throw new CertificateVerificationException("Cant get Revocation Status from OCSP.");
    }
    public static String getOcspUrl(X509Certificate certificate) throws Exception {
        byte[] octetBytes = certificate
                .getExtensionValue(Extension.authorityInfoAccess.getId());

        DLSequence dlSequence = null;
        ASN1Encodable asn1Encodable = null;

        try {
            ASN1Primitive fromExtensionValue = X509ExtensionUtil
                    .fromExtensionValue(octetBytes);
            if (!(fromExtensionValue instanceof DLSequence))
                return null;
            dlSequence = (DLSequence) fromExtensionValue;
            for (int i = 0; i < dlSequence.size(); i++) {
                asn1Encodable = dlSequence.getObjectAt(i);
                if (asn1Encodable instanceof DLSequence)
                    break;
            }
            if (!(asn1Encodable instanceof DLSequence))
                return null;
            dlSequence = (DLSequence) asn1Encodable;
            for (int i = 0; i < dlSequence.size(); i++) {
                asn1Encodable = dlSequence.getObjectAt(i);
                if (asn1Encodable instanceof DERTaggedObject)
                    break;
            }
            if (!(asn1Encodable instanceof DERTaggedObject))
                return null;
            DERTaggedObject derTaggedObject = (DERTaggedObject) asn1Encodable;
            byte[] encoded = derTaggedObject.getEncoded();
            if (derTaggedObject.getTagNo() == 6) {
                int len = encoded[1];
                return new String(encoded, 2, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private RevocationStatus getRevocationStatus(SingleResp resp) throws CertificateVerificationException {
        Object status = resp.getCertStatus();
        if (status == CertificateStatus.GOOD) {
            return RevocationStatus.GOOD;
        } else if (status instanceof RevokedStatus) {
            return RevocationStatus.REVOKED;
        } else if (status instanceof UnknownStatus) {
            return RevocationStatus.UNKNOWN;
        }
        throw new CertificateVerificationException("Cant recognize Certificate Status");
    }

    /**
     * Gets an ASN.1 encoded OCSP response (as defined in RFC 2560) from the given service URL. Currently supports
     * only HTTP.
     *
     * @param serviceUrl URL of the OCSP endpoint.
     * @param request    an OCSP request object.
     * @return OCSP response encoded in ASN.1 structure.
     * @throws CertificateVerificationException
     *
     */
    protected OCSPResp getOCSPResponce(String serviceUrl, OCSPReq request) throws CertificateVerificationException {

        try {
            //Todo: Use http client.
            byte[] array = request.getEncoded();
            if (serviceUrl.startsWith("http")) {
                HttpURLConnection con;
                URL url = new URL(serviceUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/ocsp-request");
                con.setRequestProperty("Accept", "application/ocsp-response");
                con.setDoOutput(true);
                OutputStream out = con.getOutputStream();
                DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
                dataOut.write(array);

                dataOut.flush();
                dataOut.close();

                //Check errors in response:
                if (con.getResponseCode() / 100 != 2) {
                    throw new CertificateVerificationException("Error getting ocsp response." +
                            "Response code is " + con.getResponseCode());
                }

                //Get Response
                InputStream in = (InputStream) con.getContent();
                return new OCSPResp(in);
            } else {
                throw new CertificateVerificationException("Only http is supported for ocsp calls");
            }
        } catch (IOException e) {
            throw new CertificateVerificationException("Cannot get ocspResponse from url: " + serviceUrl, e);
        }
    }

    /**
     * This method generates an OCSP Request to be sent to an OCSP endpoint.
     *
     * @param issuerCert   is the Certificate of the Issuer of the peer certificate we are interested in.
     * @param serialNumber of the peer certificate.
     * @return generated OCSP request.
     * @throws CertificateVerificationException
     *
     */
    public OCSPReq generateOCSPRequest(X509Certificate issuerCert, BigInteger serialNumber)
            throws CertificateVerificationException {

        //TODO: Have to check if this is OK with synapse implementation.
        try {
            //  CertID structure is used to uniquely identify certificates that are the subject of
            // an OCSP request or response and has an ASN.1 definition. CertID structure is defined in RFC 2560

            JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
            DigestCalculatorProvider digestCalculatorProvider = digestCalculatorProviderBuilder.build();
            DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);

            CertificateID id = new CertificateID(digestCalculator, new X509CertificateHolder(issuerCert.getEncoded()), serialNumber);

            // basic request generation with nonce
            OCSPReqBuilder generator = new OCSPReqBuilder();
            generator.addRequest(id);

            // create details for nonce extension. The nonce extension is used to bind
            // a request to a response to prevent replay attacks. As the name implies,
            // the nonce value is something that the client should only use once within a reasonably small period.
            BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());

            ExtensionsGenerator extGen = new ExtensionsGenerator();
            extGen.addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(nonce.toByteArray()));
            generator.setRequestExtensions(extGen.generate());

/*            BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
            Vector<ASN1ObjectIdentifier> objectIdentifiers = new Vector<ASN1ObjectIdentifier>();
            Vector<X509Extension> values = new Vector<X509Extension>();
            //to create the request Extension
            objectIdentifiers.add(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
            values.add(new X509Extension(false, new DEROctetString(nonce.toByteArray())));
            generator.setRequestExtensions(new X509Extensions(objectIdentifiers, values));*/

            return generator.build();
        } catch (CertificateEncodingException | IOException | OperatorCreationException|OCSPException e) {
            throw new CertificateVerificationException("Cannot generate OSCP Request with the given certificate", e);
        }
    }


 /*   *//**
     * Authority Information Access (AIA) is a non-critical extension in an X509 Certificate. This contains the
     * URL of the OCSP endpoint if one is available.
     * TODO: This might contain non OCSP urls as well. Handle this.
     *
     * @param cert is the certificate
     * @return a lit of URLs in AIA extension of the certificate which will hopefully contain an OCSP endpoint.
     * @throws CertificateVerificationException
     *
     *//*
    public static List<String> getAIALocations(X509Certificate cert) throws CertificateVerificationException {

        //Gets the DER-encoded OCTET string for the extension value for Authority information access Points
        byte[] aiaExtensionValue = cert.getExtensionValue(X509Extensions.AuthorityInfoAccess.getId());
        if (aiaExtensionValue == null)
            throw new CertificateVerificationException("Certificate Doesnt have Authority Information Access points");
        //might have to pass an ByteArrayInputStream(aiaExtensionValue)
        ASN1InputStream asn1In = new ASN1InputStream(aiaExtensionValue);
        AuthorityInformationAccess authorityInformationAccess;

        try {
            DEROctetString aiaDEROctetString = (DEROctetString) (asn1In.readObject());
            ASN1InputStream asn1Inoctets = new ASN1InputStream(aiaDEROctetString.getOctets());
            ASN1Sequence aiaASN1Sequence = (ASN1Sequence) asn1Inoctets.readObject();
            AccessDescription accessDescription = new AccessDescription();
            authorityInformationAccess = new AuthorityInformationAccess(aiaASN1Sequence);
        } catch (IOException e) {
            throw new CertificateVerificationException("Cannot read certificate to get OSCP urls", e);
        }

        List<String> ocspUrlList = new ArrayList<String>();
        AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
        for (AccessDescription accessDescription : accessDescriptions) {

            GeneralName gn = accessDescription.getAccessLocation();
            if (gn.getTagNo() == GeneralName.uniformResourceIdentifier) {
                DERIA5String str = DERIA5String.getInstance(gn.getName());
                String accessLocation = str.getString();
                ocspUrlList.add(accessLocation);
            }
        }
        if(ocspUrlList.isEmpty())
            throw new CertificateVerificationException("Cant get OCSP urls from certificate");

        return ocspUrlList;
    }*/

}
