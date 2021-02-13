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

public class Main {

    private static final PortScanner scanner = new PortScanner();

    public static void main(String[] args) {
        //scanner.simpleScan(new InetSocketAddress("5.140.164.16", 8000));
        List<String> list = readSource();
        int i = 0;
        for (String range : list) {
            scanner.prepareSinglePortScanning(range, 8000);
            scanner.scanning();

            i++;
            System.out.println("---");
            System.out.println("progress: " + i + "/" + list.size());
        }
    }

    @SneakyThrows
    public static List<String> readSource() {
        InputStream in = Main.class.getResourceAsStream("/range-list.txt");
        if (Objects.isNull(in))
            throw new FileNotFoundException("/range-list.txt");

        List<String> sources = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Stream<String> lines = reader.lines();
            lines.map(String::toLowerCase).forEach(sources::add);
            lines.close();
        }
        return sources;
    }

}