package svkreml.krekerCA.gui.params.subject;

import caJava.customOID.CustomBCStyle;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import java.util.LinkedHashMap;

public class SubjectOrder {
    LinkedHashMap<String, ASN1ObjectIdentifier> subjects = new LinkedHashMap<String, ASN1ObjectIdentifier>();

    public SubjectOrder() {
        subjects.put("CN", CustomBCStyle.CN);
        subjects.put("SURNAME", CustomBCStyle.SURNAME);
        subjects.put("GIVENNAME", CustomBCStyle.GIVENNAME);
        subjects.put("T", CustomBCStyle.T);
        subjects.put("O", CustomBCStyle.O);
        subjects.put("L", CustomBCStyle.L);
        subjects.put("ST", CustomBCStyle.ST);
        subjects.put("C", CustomBCStyle.C);
        subjects.put("E", CustomBCStyle.E);
        subjects.put("STREET", CustomBCStyle.STREET);
        subjects.put("ИНН", CustomBCStyle.ИНН);
        subjects.put("ОГРН", CustomBCStyle.ОГРН);
        subjects.put("СНИЛС", CustomBCStyle.СНИЛС);
    }

    public LinkedHashMap<String, ASN1ObjectIdentifier> getSubjects() {
        return subjects;
    }
}
/*
*   DefaultLookUp.put("инн", ИНН);
        DefaultLookUp.put("огрн", ОГРН);
        DefaultLookUp.put("снилс", СНИЛС);
        DefaultLookUp.put("c", C);
        DefaultLookUp.put("o", O);
        DefaultLookUp.put("t", T);
        DefaultLookUp.put("ou", OU);
        DefaultLookUp.put("cn", CN);
        DefaultLookUp.put("l", L);
        DefaultLookUp.put("st", ST);
        DefaultLookUp.put("sn", SN);
        DefaultLookUp.put("serialnumber", SN);
        DefaultLookUp.put("street", STREET);
        DefaultLookUp.put("emailaddress", E);
        DefaultLookUp.put("dc", DC);
        DefaultLookUp.put("e", E);
        DefaultLookUp.put("uid", UID);
        DefaultLookUp.put("surname", SURNAME);
        DefaultLookUp.put("givenname", GIVENNAME);
        DefaultLookUp.put("initials", INITIALS);
        DefaultLookUp.put("generation", GENERATION);
        DefaultLookUp.put("unstructuredaddress", UnstructuredAddress);
        DefaultLookUp.put("unstructuredname", UnstructuredName);
        DefaultLookUp.put("uniqueidentifier", UNIQUE_IDENTIFIER);
        DefaultLookUp.put("dn", DN_QUALIFIER);
        DefaultLookUp.put("pseudonym", PSEUDONYM);
        DefaultLookUp.put("postaladdress", POSTAL_ADDRESS);
        DefaultLookUp.put("nameofbirth", NAME_AT_BIRTH);
        DefaultLookUp.put("countryofcitizenship", COUNTRY_OF_CITIZENSHIP);
        DefaultLookUp.put("countryofresidence", COUNTRY_OF_RESIDENCE);
        DefaultLookUp.put("gender", GENDER);
        DefaultLookUp.put("placeofbirth", PLACE_OF_BIRTH);
        DefaultLookUp.put("dateofbirth", DATE_OF_BIRTH);
        DefaultLookUp.put("postalcode", POSTAL_CODE);
        DefaultLookUp.put("businesscategory", BUSINESS_CATEGORY);
        DefaultLookUp.put("telephonenumber", TELEPHONE_NUMBER);
        DefaultLookUp.put("name", NAME);
        DefaultLookUp.put("organizationidentifier", ORGANIZATION_IDENTIFIER);
*
* */