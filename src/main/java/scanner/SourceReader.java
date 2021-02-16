package scanner;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class SourceReader {

    @SneakyThrows
    public static List<String> readSource(String path) {
        InputStream in = new FileInputStream(new File(path));
        List<String> sources = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Stream<String> lines = reader.lines();
            lines.map(String::toLowerCase).forEach(sources::add);
            lines.close();
        }
        return sources;
    }

}