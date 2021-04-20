package scanner;

import java.util.HashMap;
import java.util.Map;

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
        prefs.put("-bw", "2000");
        prefs.put("-w", "200");
    }

    /**
     * Save all start arguments as application preferences.
     *
     * @param values list of preferences.
     */
    public static void configure(String[] values) {
        for (String value : values) {
            String[] params = value.split(":");
            prefs.put(params[0], params.length > 1 ? params[1] : "true");
        }
    }

    /**
     * Check some property value.
     *
     * @param param property name.
     * @return property state - true/false.
     */
    public static Boolean check(String param) {
        return Boolean.parseBoolean(prefs.get(param));
    }

    /**
     * Get some property value.
     *
     * @param param property name.
     * @return property value. If specified, then it, otherwise by default.
     */
    public static String get(String param) {
        return prefs.get(param);
    }

}