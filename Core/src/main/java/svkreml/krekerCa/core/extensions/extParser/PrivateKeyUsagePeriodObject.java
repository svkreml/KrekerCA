package svkreml.krekerCa.core.extensions.extParser;

import svkreml.krekerCa.core.extensions.CertBuildContainer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.PrivateKeyUsagePeriod;

import java.io.IOException;
import java.util.Date;

/*2.5.29.16 Идентификатор ключа субъекта -- Открытый ключ субъекта*/

@SuppressWarnings("WeakerAccess")
public class PrivateKeyUsagePeriodObject implements ExtensionObject {
   public final static ASN1ObjectIdentifier EXTENSION_IDENTIFIER = Extension.privateKeyUsagePeriod;
   public final static String EXTENSION_IDENTIFIER_OID = Extension.privateKeyUsagePeriod.getId();
    @Override
    public String getOid() {
        return EXTENSION_IDENTIFIER.getId();
    }


    private Prop prop = new Prop();


    /*    public PrivateKeyUsagePeriodObject(DERGeneralizedTime notBefore, DERGeneralizedTime notAfter) throws IOException {
            setValue(notBefore, notAfter);
        }
        public void setCritical(boolean critical) {
            isCritical = critical;
        }

        public ASN1Encodable getValueASN1Encodable(DERGeneralizedTime notBefore, DERGeneralizedTime notAfter) {
            return value;
        }*/
    public PrivateKeyUsagePeriodObject(boolean isCritical) {
        prop.setCritical(isCritical);
    }
    public PrivateKeyUsagePeriodObject() {
    }
    public PrivateKeyUsagePeriodObject(boolean isCritical, Date notBefore, Date notAfter) {
        prop.setNotAfter(notBefore);
        prop.setNotBefore(notAfter);
        prop.setCritical(isCritical);
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
        return new Extension(EXTENSION_IDENTIFIER, prop.isCritical(), value.toASN1Primitive().getEncoded());
    }

    @Override
    public Extension generateExtension(CertBuildContainer certBuildContainer) throws IOException {
        return getExtension();
    }

    @Override
    public void addExtension(CertBuildContainer certBuildContainer) throws IOException {
        if(prop.getNotBefore()==null||prop.getNotAfter()==null) {
            prop.setNotBefore(certBuildContainer.getFromDate());
            prop.setNotAfter(certBuildContainer.getToDate());
        }
        ASN1EncodableVector dateVector = new ASN1EncodableVector();
        DERGeneralizedTime beforeDate = new DERGeneralizedTime(prop.getNotBefore());
        dateVector.add(new DERTaggedObject(false, 0, beforeDate));
        DERGeneralizedTime untilDate = new DERGeneralizedTime(prop.getNotAfter());
        dateVector.add(new DERTaggedObject(false, 1, untilDate));
        PrivateKeyUsagePeriod value = PrivateKeyUsagePeriod.getInstance(new DERSequence(dateVector));
        certBuildContainer.getX509v3CertificateBuilder().addExtension(EXTENSION_IDENTIFIER, prop.isCritical(), value.toASN1Primitive().getEncoded());
    }

    class Prop {
        Date notBefore;
        Date notAfter;
        boolean isCritical = false; //default

        public boolean isCritical() {
            return isCritical;
        }

        public void setCritical(boolean critical) {
            isCritical = critical;
        }

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
