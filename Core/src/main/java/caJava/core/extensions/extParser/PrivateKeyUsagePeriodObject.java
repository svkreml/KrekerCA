package caJava.core.extensions.extParser;

import caJava.core.extensions.CertBuildContainer;
import caJava.fileManagement.Json;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.PrivateKeyUsagePeriod;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;

/*2.5.29.16 Идентификатор ключа субъекта -- Открытый ключ субъекта*/

public class PrivateKeyUsagePeriodObject {

    String oid = Extension.privateKeyUsagePeriod.getId();
    boolean isCritical = false;//default
    Prop prop = new Prop();





    public static void main(String[] args) throws IOException, ParseException {
        ASN1EncodableVector dateVector = new ASN1EncodableVector();
        DERGeneralizedTime beforeDate = new DERGeneralizedTime(new Date());
        dateVector.add(new DERTaggedObject(false, 0, beforeDate));
        DERGeneralizedTime untilDate = new DERGeneralizedTime(new Date());
        dateVector.add(new DERTaggedObject(false, 1, untilDate));
        PrivateKeyUsagePeriod value = PrivateKeyUsagePeriod.getInstance(new DERSequence(dateVector));
        PrivateKeyUsagePeriodObject privateKeyUsagePeriodObject = new PrivateKeyUsagePeriodObject(new Date(), new Date());

        System.out.println(Json.write(privateKeyUsagePeriodObject));

        LinkedHashMap<String, Object> readValue = (LinkedHashMap<String, Object>) Json.readValue(LinkedHashMap.class, "{\"notBefore\":\"20181113185547GMT+00:00\", \"notAfter\":\"20181113185547GMT+00:00\"}");


    }
/*    public PrivateKeyUsagePeriodObject(DERGeneralizedTime notBefore, DERGeneralizedTime notAfter) throws IOException {
        setValue(notBefore, notAfter);
    }
    public void setCritical(boolean critical) {
        isCritical = critical;
    }

    public ASN1Encodable getValueASN1Encodable(DERGeneralizedTime notBefore, DERGeneralizedTime notAfter) {
        return value;
    }*/

    public PrivateKeyUsagePeriodObject(Date notBefore, Date notAfter) {
        prop.setNotAfter(notBefore);
        prop.setNotBefore(notAfter);
    }

/*    public void setValue(String valueJson) throws IOException {
        LinkedHashMap<String, Object> readValue = (LinkedHashMap<String, Object>) Json.readValue(Object.class, valueJson);
        setValue(new DERGeneralizedTime(readValue.get("notBefore").toString()), new DERGeneralizedTime(readValue.get("notAfter").toString()));
    }*/


    @JsonIgnore
    public Extension getExtension() throws IOException {
        ASN1EncodableVector dateVector = new ASN1EncodableVector();
        DERGeneralizedTime beforeDate = new DERGeneralizedTime(prop.getNotBefore());
        dateVector.add(new DERTaggedObject(false, 0, beforeDate));
        DERGeneralizedTime untilDate = new DERGeneralizedTime(prop.getNotAfter());
        dateVector.add(new DERTaggedObject(false, 1, untilDate));
        PrivateKeyUsagePeriod value = PrivateKeyUsagePeriod.getInstance(new DERSequence(dateVector));
        Extension extension = new Extension(Extension.privateKeyUsagePeriod, isCritical, value.toASN1Primitive().getEncoded());
        return extension;
    }

    class Prop {
        Date notBefore;
        Date notAfter;

        public Date getNotBefore() {
            return notBefore;
        }

        public void setNotBefore(Date notBefore) {
            this.notBefore = notBefore;
        }

        public Date getNotAfter() {
            return notAfter;
        }

        public void setNotAfter(Date notAfter) {
            this.notAfter = notAfter;
        }
    }
}
