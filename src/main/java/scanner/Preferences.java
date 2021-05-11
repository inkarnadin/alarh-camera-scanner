package scanner;

import lombok.Getter;

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
        prefs.put("-t", "20");
        prefs.put("-a", "5");
        prefs.put("-bw", "2000");
        prefs.put("-w", "500");
        prefs.put("-recovery_scanning", "true");
    }

    private static final List<String> defaultPasswordList = Collections.singletonList("asdf1234");

    @Getter
    private static List<String> rangesList;
    @Getter
    private static List<String> passwordsList;

    /**
     * Save all start arguments as application preferences.
     *
     * <p> Prepares a range of addresses for validation specified under the flag <b>-source</b>.
     * If no range is specified, the application exits.
     *
     * <p> Prepares a list of passwords for brute force attack specified under the flag <b>-passwords</b>.
     * If no range is specified, the application use single default password <b>asdf1234</b>.
     *
     * @param values input application args
     */
    public static void configure(String[] values) {
        for (String value : values) {
            String[] params = value.split(":");
            prefs.put(params[0], params.length > 1 ? params[1] : "true");
        }

        String source = prefs.get("-source");
        String passwords = prefs.get("-passwords");

        if (Objects.isNull(source)) {
            System.out.println("Source list cannot be empty!");
            System.exit(0);
        }

        rangesList = SourceReader.readSource(source);
        passwordsList = SourceReader.readSource(passwords);

        if (passwordsList.isEmpty())
            passwordsList.addAll(defaultPasswordList);
    }

    /**
     * Check some property value.
     *
     * @param value property name
     * @return property state - true/false
     */
    public static boolean check(String value) {
        return Boolean.parseBoolean(prefs.get(value));
    }

    /**
     * Get some property value.
     *
     * @param value property name
     * @return property value. If specified, then it, otherwise by default
     */
    public static String get(String value) {
        return prefs.get(value);
    }

    /**
     * Change some property value.
     *
     * @param value property name
     */
    public static void change(String key, String value) {
        prefs.put(key, value);
    }

}