package svkreml.krekerCa.core.extensions;

import java.util.ArrayList;

@Deprecated
public class ExtensionParam {
    public String name;
    public String[] params;

    public ExtensionParam(String name) {
        this.name = name;
    }

    public ExtensionParam(String name, String ... params) {
        this.name = name;
        this.params = params;
    }
    public ExtensionParam(String name, ArrayList<String> params) {
        this.name = name;
        this.params = params.toArray(new String[0]);
    }
}
