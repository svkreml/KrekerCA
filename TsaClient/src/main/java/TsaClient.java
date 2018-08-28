/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.tsp.*;
import org.bouncycastle.util.Store;
import org.testng.log4testng.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;


/**
 * Time Stamping Authority (TSA) Client [RFC 3161].
 *
 * @author Vakhtang Koroghlishvili
 * @author John Hewson
 */
public class TsaClient {

    private static final Logger LOG = Logger.getLogger(TsaClient.class);
    private final URL url;
    private final String username;
    private final String password;
    private final MessageDigest digest;
    X500NameStyle x500NameStyle = RFC4519Style.INSTANCE;

    /**
     * @param url      the URL of the TSA service
     * @param username user name of TSA
     * @param password password of TSA
     * @param digest   the message digest to use
     */
    public TsaClient(URL url, String username, String password, MessageDigest digest) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.digest = digest;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * @param messageImprint imprint of message contents
     * @return the encoded time stamp token
     * @throws IOException if there was an error with the connection or data from the TSA server,
     *                     or if the time stamp response could not be validated
     */
    public TimeStampToken getTimeStampToken(byte[] messageImprint) throws IOException {
        digest.reset();
        byte[] hash = digest.digest(messageImprint);

        // 32-bit cryptographic nonce
        SecureRandom random = new SecureRandom();
        int nonce = random.nextInt();

        // generate TSA request
        TimeStampRequestGenerator tsaGenerator = new TimeStampRequestGenerator();
        tsaGenerator.setCertReq(true);
        ASN1ObjectIdentifier oid = getHashObjectIdentifier(digest.getAlgorithm());
        TimeStampRequest request = tsaGenerator.generate(oid, hash, BigInteger.valueOf(nonce));

        // get TSA response
        byte[] tsaResponse = getTSAResponse(request.getEncoded());

        TimeStampResponse response;
        try {
            response = new TimeStampResponse(tsaResponse);
            response.validate(request);
        } catch (TSPException e) {
            throw new IOException(e);
        }

        TimeStampToken token = response.getTimeStampToken();
        if (token == null) {
            throw new IOException("Response does not have a time stamp token");
        }

        return token;
    }
    public TimeStampToken getTimeStampTokenFromHash(byte[] hash) throws IOException {
        // 32-bit cryptographic nonce
        SecureRandom random = new SecureRandom();
        int nonce = random.nextInt();
        // generate TSA request
        TimeStampRequestGenerator tsaGenerator = new TimeStampRequestGenerator();
        tsaGenerator.setCertReq(true);
        ASN1ObjectIdentifier oid = getHashObjectIdentifier(digest.getAlgorithm());
        TimeStampRequest request = tsaGenerator.generate(oid, hash, BigInteger.valueOf(nonce));

        // get TSA response
        byte[] tsaResponse = getTSAResponse(request.getEncoded());

        TimeStampResponse response;
        try {
            response = new TimeStampResponse(tsaResponse);
            response.validate(request);
        } catch (TSPException e) {
            throw new IOException(e);
        }

        TimeStampToken token = response.getTimeStampToken();
        if (token == null) {
            throw new IOException("Response does not have a time stamp token");
        }

        return token;
    }
    // gets response data for the given encoded TimeStampRequest data
    // throws IOException if a connection to the TSA cannot be established
    private byte[] getTSAResponse(byte[] request) throws IOException {
        LOG.debug("Opening connection to TSA server");

        // todo: support proxy servers
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/timestamp-query");

        LOG.debug("Established connection to TSA server");

        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            connection.setRequestProperty(username, password);
        }

        // read response
        OutputStream output = null;
        try {
            output = connection.getOutputStream();
            output.write(request);
        } finally {

        }

        LOG.debug("Waiting for response from TSA server");

        InputStream input = null;
        byte[] response;
        try {
            input = connection.getInputStream();
            response = IOUtils.toByteArray(input);
        } finally {

        }


        LOG.debug("Received response from TSA server");

        return response;
    }

    public boolean validateTokenTimestamp(TimeStampToken token, byte[] digest) {
        byte[] messageImprintDigest = token.getTimeStampInfo().getMessageImprintDigest();
        //System.out.println("tsp Digest   " + new BigInteger(messageImprintDigest).toString(16));
        //System.out.println("local Digest " + new BigInteger(digest).toString(16));
        return Arrays.equals(messageImprintDigest, digest);
    }


    public void printTokenInfo(TimeStampToken token) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        System.out.print("Время генераци: ");
        System.out.println(token.getTimeStampInfo().getGenTime());
        System.out.print("Используемый алгоритм при генерации хэша: ");
        System.out.println(token.getTimeStampInfo().getMessageImprintAlgOID());
/*        System.out.print("Используемый алгоритм подписи: ");
        token.toCMSSignedData().getSignerInfos().iterator().forEachRemaining(
                (SignerInformation signerInformation) -> {
                   // X509CertificateHolder clientCertificate = null;
                    try {
                        *//*Валидация штампа времени*//*
                        SignerInformationVerifier signerInformationVerifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build();
                        token.validate(signerInformationVerifier);
                        System.out.println("Штамп времени действителен");
                    } catch (CertificateException | OperatorCreationException | TSPException e) {
                        System.out.println("Штамп времени недействителен: " + e.getLocalizedMessage());
                    }
                }
        );*/
        System.out.print("Политика сервера штампов времени: ");
        System.out.println( token.getTimeStampInfo().getPolicy().getId());
        System.out.println("Цепочка сертификатов сервисов штампов времени:[");
        Store certificates = token.getCertificates();


        certificates.getMatches(null).iterator().forEachRemaining((Object o) ->
        {
            X509CertificateHolder x509CertificateHolder = (X509CertificateHolder) o;
            System.out.println("Субъект:\n" + x509CertificateHolder.getSubject());
            System.out.println("Издатель:\n" + x509CertificateHolder.getIssuer());
            System.out.println("Серийный номер: " + x509CertificateHolder.getSerialNumber());
            System.out.println("Действителен с " + x509CertificateHolder.getNotBefore() + " по " + x509CertificateHolder.getNotAfter());
            System.out.println("Алгоритм цифровой подписи: " + x509CertificateHolder.getSignatureAlgorithm().getAlgorithm().getId());
        });
        System.out.println("]");
    }

    // returns the ASN.1 OID of the given hash algorithm
    private ASN1ObjectIdentifier getHashObjectIdentifier(String algorithm) {
        switch (algorithm) {
            case "MD2":
                return new ASN1ObjectIdentifier(PKCSObjectIdentifiers.md2.getId());
            case "MD5":
                return new ASN1ObjectIdentifier(PKCSObjectIdentifiers.md5.getId());
            case "SHA-1":
                return new ASN1ObjectIdentifier(OIWObjectIdentifiers.idSHA1.getId());
            case "SHA-224":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha224.getId());
            case "SHA-256":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha256.getId());
            case "SHA-384":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha384.getId());
            case "SHA-512":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha512.getId());
            /*
             * Российские стандарты https://cpdn.cryptopro.ru/content/csp40/html/group___pro_c_s_p_ex_DP8.html
             * */
            case "GOST3411":
                return new ASN1ObjectIdentifier(CryptoProObjectIdentifiers.gostR3411.getId());//1.2.643.2.2.9
            case "GOST3411-2012-256":
            case "GOST3411_2012_256":
                return new ASN1ObjectIdentifier("1.2.643.7.1.1.2.2");
            case "GOST3411-2012-512":
            case "GOST3411_2012_512":
                return new ASN1ObjectIdentifier("1.2.643.7.1.1.2.3");
            default:
                return new ASN1ObjectIdentifier(algorithm);
        }
    }
}