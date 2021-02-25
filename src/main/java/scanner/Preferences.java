package scanner;

import java.util.*;

/**
 * Global settings class.
 *
 * @author inkarnadin
 */
public class Preferences {

    private static final Map<String, String> prefs = new HashMap<>();

    static {
        prefs.put("-p", "554");
        prefs.put("-t", "10");
        prefs.put("-a", "5");
    }

    public static void configure(String[] values) {
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

}