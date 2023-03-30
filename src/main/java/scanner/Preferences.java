package scanner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

/**
 * Класс глобальных конфигурация приложения.
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
     * Метод сохранения всех свойств, вычитанных из файла, в память приложения.
     * <p>Подготавливает для проверки диапазон адресов, указанных под флагом <b>-source</b>.
     * Если диапазон не указан, приложение закрывается.
     * <p>Подготавливает список паролей для атаки методом полного перебора, указанного под флагом <b>-passwords</b>.
     * Если диапазон не указан, приложение использует один пароль по умолчанию. <b>asdf1234</b>.
     *
     * @param values свойства приложения
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
     * Метод конвертации свойств двоичного характера.
     *
     * @param value значение
     * @return конвертированное значение - true/false
     */
    public static boolean parseBoolean(String value) {
        return Boolean.parseBoolean(prefs.get(value));
    }

    /**
     * Метод получения значения свойства.
     *
     * @param value значение
     * @return значение в виде строки
     */
    public static String get(String value) {
        return prefs.get(value);
    }

    /**
     * Метод изменения значения свойства.
     *
     * @param value новое значение
     */
    public static void change(String key, String value) {
        prefs.put(key, value);
    }

    /**
     * Метод создания директории.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void createDirectories() {
        new File("result/screen").mkdir();
    }

}