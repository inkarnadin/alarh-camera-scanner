package scanner.recover;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.SourceReader;
import scanner.stat.ScanStatItem;
import scanner.stat.ScreenStatItem;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static scanner.Preferences.*;
import static scanner.recover.RecoveryElement.*;
import static scanner.stat.StatDataHolder.SCAN_GATHERER;
import static scanner.stat.StatDataHolder.SCREEN_GATHERER;

/**
 * Class for save and load interrupted checking process.
 *
 * @author inkarnadin
 * on 11-05-2021
 */
@Slf4j
public final class RecoveryManager {

    private static final Map<RecoveryElement, String> BUFFERED_DATA = new HashMap<>();
    private static Map<RecoveryElement, String> restoredData = new HashMap<>();

    private static final String SPLITTER = "/";
    private static final File BACKUP = new File("backup.tmp");

    private RecoveryManager() {
        throw new AssertionError("Utility class can't be instantiate");
    }

    /**
     * Method for saving some value for later recovery.
     *
     * @param element recover element
     * @param value checking range
     */
    public static void save(RecoveryElement element, String value) {
        BUFFERED_DATA.put(element, value);
        flush();
    }

    /**
     * Method for writing data to a backup file.
     */
    public static void flush() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(BACKUP)))) {
            for (Map.Entry<RecoveryElement, String> item : BUFFERED_DATA.entrySet()) {
                bw.write(item.getKey().getDescription());
                bw.write(SPLITTER);
                bw.write(item.getValue());
                bw.newLine();
            }
        } catch (Exception xep) {
            log.warn("error during save state: {}", xep.getMessage());
        }
    }

    /**
     * Method for recovering data from a backup file to restore the state of the application.
     * Only if checksum source file equals.
     *
     * After restoring, the backup file will be overwritten.
     */
    @SneakyThrows
    public static void recover() {
        String currentSourceHash = SourceReader.checksum(get("-source"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(BACKUP)))) {
            restoredData = reader.lines()
                    .map(x -> x.split(SPLITTER))
                    .filter(f -> f.length == 2)
                    .collect(Collectors.toMap(k -> RecoveryElement.find(k[0]), v -> v[1]));

            String savedSourceHash = restoredData.get(SOURCE_CHECKSUM);
            if (!currentSourceHash.equals(savedSourceHash))
                Preferences.change(ALLOW_RECOVERY_SCANNING, Boolean.FALSE.toString());

            if (check(ALLOW_RECOVERY_SCANNING)) {
                String scanStats = restoredData.get(SCANNING_STAT);
                if (Objects.nonNull(scanStats)) {
                    String[] values = restoredData.get(SCANNING_STAT).split(";");
                    for (ScanStatItem e : ScanStatItem.values())
                        SCAN_GATHERER.set(e, Long.parseLong(values[e.ordinal()]));
                }

                String screenStats = restoredData.get(SCREENING_STAT);
                if (Objects.nonNull(screenStats)) {
                    String[] values = restoredData.get(SCREENING_STAT).split(";");
                    for (ScreenStatItem e : ScreenStatItem.values())
                        SCREEN_GATHERER.set(e, Long.parseLong(values[e.ordinal()]));
                }
            }
            log.info("previous session was restored");
        } catch (FileNotFoundException ignored) {
        } catch (Exception xep) {
            log.warn("error during restore state: {}", xep.getMessage());
        }
        save(SOURCE_CHECKSUM, currentSourceHash);
    }

    /**
     * Method to getting recovered value by recovery key.
     *
     * @param element recovery key
     * @return recovered element
     */
    public static String getRestoredValue(RecoveryElement element) {
        return restoredData.get(element);
    }

    /**
     * Method for Drop backup file if successfully exit.
     */
    public static void dropBackup() {
        if (BACKUP.delete())
            log.info("backup file was removed.");
    }

}