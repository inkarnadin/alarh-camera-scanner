package scanner;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SourceReader {

    @SneakyThrows
    public static List<String> readSource(String path) {
        InputStream in = Main.class.getResourceAsStream(path);
        if (Objects.isNull(in))
            throw new FileNotFoundException(path);

        List<String> sources = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Stream<String> lines = reader.lines();
            lines.map(String::toLowerCase).forEach(sources::add);
            lines.close();
        }
        return sources;
    }

}