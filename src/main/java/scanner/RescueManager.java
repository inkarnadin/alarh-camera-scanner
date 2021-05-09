package scanner;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.http.InetSocketAddressRange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Save and load interrupted checking process.
 * @author inkarnadin
 */
@Slf4j
public class RescueManager {

    @Getter
    private static String stopAddress = "";

    private static final File backup = new File("backup.tmp");

    /**
     * Saves the beginning of the range before checking for later recovery.
     *
     * @param value checking range
     */
    public static void save(InetSocketAddressRange value) {
        if (Objects.nonNull(value)) {
            try (OutputStream out = new FileOutputStream(backup)) {
                out.write(value.first().getHostString().getBytes());
            } catch (Exception xep) {
                log.warn("Error during save scanning state: {}", xep.getMessage());
            }
        }
    }

    /**
     * Recovers data from a backup file to restore the state of the application.
     */
    @SneakyThrows
    public static void restore() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(backup)))) {
            Stream<String> lines = reader.lines();
            lines.findFirst().ifPresent(s -> stopAddress = s);
            lines.close();
        } catch (FileNotFoundException ignored) {
        } catch (Exception xep) {
            log.warn("Error during save scanning state: {}", xep.getMessage());
        }
        log.info("Previous session was restored");
    }

}