package scanner;

import java.util.*;

public class Preferences {

    public static final Map<String, String> prefs = new HashMap<>();

    public static void configure(String[] values) {
        setDefaultValues();
        for (String value : values) {
            String[] params = value.split(":");
            prefs.put(params[0], params.length > 1 ? params[1] : "true");
        }
    }

    public static Boolean check(String param) {
        return Boolean.parseBoolean(prefs.get(param));
    }

    public static String get(String param) {
        return prefs.get(param);
    }

    private static void setDefaultValues() {
        prefs.put("-p", "554");
        prefs.put("-t", "25");
    }

}