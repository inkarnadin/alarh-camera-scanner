package scanner;

import java.util.*;

public class Preferences {

    public static final Map<String, String> prefs = new HashMap<>();

    public static void configure(String[] values) {
        for (String value : values) {
            String[] params = value.split(":");
            prefs.put(params[0], params.length > 1 ? params[1] : "true");
        }
    }

    public static Boolean check(String param) {
        return Boolean.parseBoolean(prefs.get(param));
    }

    public static Optional<String> get(String param) {
        return Optional.ofNullable(prefs.get(param));
    }

}