package scanner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

/**
 * Global settings class.
 *
 * @author inkarnadin
 */
@Slf4j
public class Preferences {

    public static final String PORT = "-p";
    public static final String THREADS = "-th";
    public static final String SOCKET_WAITING = "-w";
    public static final String SOCKET_TIMEOUT = "-t";
    public static final String CONNECTION_ATTEMPT = "-a";

    public static final String RANGE_PATH = "-source";
    public static final String PASSWORD_PATH = "-passwords";

    public static final String NO_SCANNING = "-nc";
    public static final String NO_BRUTE = "-nb";
    public static final String ALLOW_UNTRUSTED_HOST = "-uc";
    public static final String ALLOW_FRAME_SAVING = "-sf";

    public static final String ALLOW_RECOVERY_SCANNING = "-recovery_scanning";

    private static final Map<String, String> prefs = new HashMap<>();

    static {
        prefs.put(PORT, "554");
        prefs.put(THREADS, "10");
        prefs.put(CONNECTION_ATTEMPT, "5");
        prefs.put(SOCKET_WAITING, "2000");
        prefs.put(SOCKET_TIMEOUT, "500");
        prefs.put(ALLOW_RECOVERY_SCANNING, "true");
    }

    private static final List<String> defaultPasswordList = Collections.singletonList("asdf1234");

    @Getter
    private static Set<String> rangesList;
    @Getter
    private static Set<String> passwordsList;

    /**
     * Save all start arguments as application preferences.
     *
     * <p> Prepares a range of addresses for validation specified under the flag <b>-source</b>.
     * If no range is specified, the application exits.
     *
     * <p> Prepares a list of passwords for brute force attack specified under the flag <b>-passwords</b>.
     * If no range is specified, the application use single default password <b>asdf1234</b>.
     *
     * <p> Configure some application settings such as needed directory structures.
     *
     * @param values input application args
     */
    public static void configure(String[] values) {
        for (String value : values) {
            String[] params = value.split(":");
            prefs.put(params[0], params.length > 1 ? params[1] : "true");
        }

        String source = prefs.get(RANGE_PATH);
        String passwords = prefs.get(PASSWORD_PATH);

        if (Objects.isNull(source)) {
            System.out.println("Source list cannot be empty!");
            log.warn("source list is empty - exit");
            System.exit(0);
        }

        rangesList = SourceReader.readSource(source);
        passwordsList = SourceReader.readSource(passwords);

        if (passwordsList.isEmpty())
            passwordsList.addAll(defaultPasswordList);

        createDirectories();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void createDirectories() {
        new File("result/screen").mkdir();
    }

}