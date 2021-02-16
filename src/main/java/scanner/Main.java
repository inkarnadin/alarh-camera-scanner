package scanner;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Main {

    public static void main(String[] args) {
        System.out.println("Usage: ");
        System.out.println("  java -jar port-scanner.jar -b source:/home/user/list.txt");
        System.out.println("  java -jar port-scanner.jar -c source:/home/user/list.txt");

        List<String> params = Arrays.asList(args);
        Optional<String> source = params.stream().filter(s -> s.contains("source")).findFirst();

        if (source.isEmpty()) {
            log.error("source cannot be empty!");
            System.exit(0);
        }

        String path = source.get().split(":")[1];
        List<String> list = SourceReader.readSource(path);

        if (params.contains("-b")) {
            final BruteForceScanner bruteForceScanner = new BruteForceScanner();
            int i = 0;
            for (String ip : list) {
                bruteForceScanner.brute(ip);
                log.info("progress: " + ++i + "/" + list.size());
            }
        }

        if (params.contains("-c")) {
            final CameraScanner scanner = new CameraScanner();
            int c = 0;
            for (String range : list) {
                scanner.prepareSinglePortScanning(range, 8000);
                scanner.scanning();
                log.info("progress: " + ++c + "/" + list.size());
            }
        }
        System.exit(0);
    }

}