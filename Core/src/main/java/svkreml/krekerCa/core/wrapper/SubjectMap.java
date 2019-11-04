package svkreml.krekerCa.core.wrapper;

import svkreml.krekerCa.customOID.CustomBCStyle;
import svkreml.krekerCa.fileManagement.Json;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/*хз зачем*/
public class SubjectMap {

    public static X500Name load(File file) throws IOException {
        X500NameBuilder x500NameBld = new X500NameBuilder(CustomBCStyle.INSTANCE);
        Map<String, String> map = (Map<String, String>) Json.readValue(LinkedHashMap.class, file);
        map.forEach((String oid, String value) -> {
            x500NameBld.addRDN(CustomBCStyle.DefaultLookUp.get(oid.toLowerCase()), value);
        });
        return x500NameBld.build();
    }
    public static X500Name convert(Map<String, String> x500) {
        X500NameBuilder x500NameBld = new X500NameBuilder(CustomBCStyle.INSTANCE);
        x500.forEach((String oid, String value) -> {
            x500NameBld.addRDN(CustomBCStyle.DefaultLookUp.get(oid.toLowerCase()), value);
        });
        return x500NameBld.build();
    }
    public static Map<String, String> convert(X500Name x500Name) {
        Map<String, String> map = new LinkedHashMap<>();
        RDN[] rdNs = x500Name.getRDNs();
 /*       RDN[] rdNsReverse = new RDN[rdNs.length];
        for (int i = 0; i < rdNs.length; i++) {
            rdNsReverse[i]=rdNs[rdNs.length-i-1];
        }*/

        for (RDN rdN : rdNs) {
            //System.out.print(rdN.getFirst().getType()+"  ");
            //System.out.println(rdN.getFirst().getValue());
            map.put(CustomBCStyle.DefaultSymbols.get(new ASN1ObjectIdentifier(rdN.getFirst().getType().toString()).intern()).toString(), rdN.getFirst().getValue().toString());
        }
        return map;
    }

    public static void save(X500Name x500Name, File file) throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        RDN[] rdNs = x500Name.getRDNs();
        for (RDN rdN : rdNs) {
            //System.out.print(rdN.getFirst().getType()+"  ");
            //System.out.println(rdN.getFirst().getValue());
            map.put(CustomBCStyle.DefaultSymbols.get(new ASN1ObjectIdentifier(rdN.getFirst().getType().toString()).intern()).toString(), rdN.getFirst().getValue().toString());
        }
        //System.out.println(map);
        Json.write(map, file);
    }
}
