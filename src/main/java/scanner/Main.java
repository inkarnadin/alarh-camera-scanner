package scanner;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {
        List<String> params = Arrays.asList(args);

        if (params.contains("-b")) {
            List<String> list = SourceReader.readSource("/list.txt");

            final BruteForceScanner bruteForceScanner = new BruteForceScanner();
            int i = 0;
            for (String ip : list) {
                bruteForceScanner.brute(ip);
                log.info("progress: " + ++i + "/" + list.size());
            }
        }

        if (params.contains("-c")) {
            List<String> list = SourceReader.readSource("/range-list.txt");

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