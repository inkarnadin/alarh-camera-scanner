package scanner.recover;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.SourceReader;
import scanner.stat.ScanStatItem;
import scanner.stat.ScreenStatItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static scanner.Preferences.ALLOW_RECOVERY_SCANNING;
import static scanner.Preferences.parseBoolean;
import static scanner.Preferences.get;
import static scanner.recover.RecoveryElement.SCANNING_STAT;
import static scanner.recover.RecoveryElement.SCREENING_STAT;
import static scanner.recover.RecoveryElement.SOURCE_CHECKSUM;
import static scanner.stat.StatDataHolder.SCAN_GATHERER;
import static scanner.stat.StatDataHolder.SCREEN_GATHERER;

/**
 * Класс управления сохранением и загрузкой процесса восстановления сессия из бекапа.
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
     * Метод сохранения информации для восстановления.
     *
     * @param element элемент восстановления
     * @param value сохраняемое значение
     */
    public static void save(RecoveryElement element, String value) {
        BUFFERED_DATA.put(element, value);
        flush();
    }

    /**
     * Метод для записи данных в файл восстановления.
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
     * Метод восстановления данных из бекап-файла для получения последнего состояния приложения до прерывания его работы.
     * <p>Восстановление будет произведено только в случае совпадения контрольной суммы входного файла параметров.
     * <p>После восстановления бекап-файл будет перезаписан.
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

            if (parseBoolean(ALLOW_RECOVERY_SCANNING)) {
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
     * Метод получения данных для восстановления по ключу.
     *
     * @param element элемент восстановления
     * @return восстановленное значение элемента
     */
    public static String getRestoredValue(RecoveryElement element) {
        return restoredData.get(element);
    }

    /**
     * Метод удаления бекап-файла в случае успешного завершения рабочей сессии приложения.
     */
    public static void dropBackup() {
        if (BACKUP.delete())
            log.info("backup file was removed.");
    }

}