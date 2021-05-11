package scanner.recover;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.SourceReader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static scanner.recover.RecoveryElement.SOURCE_CHECKSUM;

/**
 * Save and load interrupted checking process.
 * @author inkarnadin
 */
@Slf4j
public class RecoveryManager {

    private static final Map<RecoveryElement, String> bufferedData = new HashMap<>();
    private static Map<RecoveryElement, String> restoredData = new HashMap<>();

    private static final String splitter = "/";

    private static final File backup = new File("backup.tmp");

    /**
     * Initiate restore process for current executable session.
     * Save checksum <b>source</b>-file.
     */
    public static void initNewRestoreProcess() {
        String savedSourceHash = restoredData.get(SOURCE_CHECKSUM);
        String newSourceHash = SourceReader.checksum(Preferences.get("-source"));

        save(SOURCE_CHECKSUM, newSourceHash);

        if (!newSourceHash.equals(savedSourceHash))
            Preferences.change("-recovery_scanning", "false");
    }

    /**
     * Save some value for later recovery.
     *
     * @param element recover element
     * @param value checking range
     */
    public static void save(RecoveryElement element, String value) {
        bufferedData.put(element, value);
        flush();
    }

    /**
     * Write data to backup file.
     */
    public static void flush() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(backup)))) {
            for (Map.Entry<RecoveryElement, String> item : bufferedData.entrySet()) {
                bw.write(item.getKey().getDescription());
                bw.write(splitter);
                bw.write(item.getValue());
                bw.newLine();
            }
        } catch (Exception xep) {
            log.warn("Error during save state: {}", xep.getMessage());
        }
    }

    /**
     * Recovers data from a backup file to restore the state of the application.
     * Only if checksum source file equals.
     *
     * After restoring, the backup file will be overwritten.
     */
    @SneakyThrows
    public static void recover() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(backup)))) {
            restoredData = reader.lines()
                    .map(x -> x.split(splitter))
                    .filter(f -> f.length == 2)
                    .collect(Collectors.toMap(k -> RecoveryElement.find(k[0]), v -> v[1]));
            log.info("Previous session was restored");
        } catch (FileNotFoundException ignored) {
        } catch (Exception xep) {
            log.warn("Error during restore state: {}", xep.getMessage());
        }
    }

    /**
     * Get recovered value by recovery key.
     *
     * @param element recovery key
     * @return recovered element
     */
    public static String getRestoredValue(RecoveryElement element) {
        return restoredData.get(element);
    }

    /**
     * Drop backup file if successfully exit.
     */
    public static void drop() {
        if (backup.delete())
            log.info("Backup file was removed.");
    }

}