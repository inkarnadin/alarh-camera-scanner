package scanner;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

/**
 * Компонент чтения файла данных.
 *
 * @author inkarnadin
 * on 16-02-2021
 */
@Slf4j
public final class SourceReader {

    private SourceReader() {
        throw new AssertionError("Utility class can't be instantiate");
    }

    /**
     * Получения листа значения специально сформированного файла данных.
     *
     * @param path путь размещения файла
     * @return лист значений, вернет пустой список, если файл пустой
     */
    public static Set<String> readSource(String path) {
        Set<String> sources = new TreeSet<>();
        try {
            if (Objects.isNull(path))
                return sources;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
                Stream<String> lines = reader.lines();
                lines.forEach(sources::add);
                lines.close();
            }
            return sources;
        } catch (Exception xep) {
            log.error("Error during file {} opening: {}", path, xep.getMessage());
        }

        return sources;
    }

    /**
     * Метод расчета контрольной суммы исходного файла.
     * <p>Основное назначение - восстановление прерванной сессии.
     *
     * @param path путь размещения файла
     */
    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public static String checksum(String path) {
        try {
            HashCode checksum = Files.asByteSource(new File(path)).hash(Hashing.md5());
            return checksum.toString().toUpperCase();
        } catch (Exception xep) {
            log.error("Error during calculate checksum: {}", xep.getMessage());
        }
        return "";
    }

}