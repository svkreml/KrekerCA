package svkreml.krekerCa.core.wrapper;

import svkreml.krekerCa.core.extensions.ExtensionParam;
import svkreml.krekerCa.fileManagement.Json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

/*хз зачем*/
public class ExtensionsMap {
    public static void main(String[] args) throws IOException {
        Vector<ExtensionParam> extensions = new Vector<>();
        extensions.add(new ExtensionParam("keyUsage", "false", "86"));
        extensions.add(new ExtensionParam("subjectKeyIdentifier", "false"));
        //extensions.add(new ExtensionParam("1.3.6.1.4.1.311.20.2", "false", "subCA"));
        extensions.add(new ExtensionParam("certificatePolicies", "false", "1.2.643.100.113.1"));
        extensions.add(new ExtensionParam("subjectSignTool", "false", "123123123123"));
        extensions.add(new ExtensionParam("1.3.6.1.4.1.311.21.1", "false", "0"));
        extensions.add(new ExtensionParam("authorityKeyIdentifier", "false"));
        extensions.add(new ExtensionParam("cRLDistributionPoints", "false", "http://localhost/revoked.crl"));
        extensions.add(new ExtensionParam("issuerSignTool", "false", "123123123123", "123123123123", "123123123123", "123123123123123123"));
        extensions.add(new ExtensionParam("basicConstraints", "true", "0"));


        File file = new File("extensions.json");

        ExtensionsMap.set(extensions, file);

        LinkedHashMap<String, ArrayList<String>> extensionParams = ExtensionsMap.get(file);
        System.out.println(extensionParams);
    }

    @Deprecated
    public static Vector<ExtensionParam> getVector(File file) throws IOException {
        LinkedHashMap<String, ArrayList<String>> map = get(file);
        Vector<ExtensionParam> vector = new Vector<>();
        map.forEach((String k, ArrayList<String> v) -> {
                vector.add(new ExtensionParam(k, v));
        });
        return vector;
    }


    public static LinkedHashMap<String, ArrayList<String>> get(File file) throws IOException {
        Object value = Json.readValue(LinkedHashMap.class, file);
        return (LinkedHashMap<String, ArrayList<String>>) value;
    }

    public static void set(Vector<ExtensionParam> extensions, File file) throws IOException {
        Map<String, String[]> map = new LinkedHashMap<>();
        for (ExtensionParam extensionParam : extensions) {
            //System.out.print(rdN.getFirst().getType()+"  ");
            //System.out.println(rdN.getFirst().getValue());
            map.put(extensionParam.name, extensionParam.params);
        }
        System.out.println(map);
        Json.write(map, file);
    }

}
