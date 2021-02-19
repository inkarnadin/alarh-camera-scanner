package scanner;

import lombok.extern.slf4j.Slf4j;
import scanner.brute.BruteForceScanner;
import scanner.brute.IpBruteFilter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Preferences.configure(args);

        Optional<String> source = Preferences.get("-source");
        Optional<String> passwords = Preferences.get("-passwords");

        if (source.isEmpty()) {
            log.error("source cannot be empty!");
            System.exit(0);
        }

        List<String> listSources = SourceReader.readSource(source.get().split(":")[1]);
        List<String> listPasswords = passwords
                .map(s -> SourceReader.readSource(s.split(":")[1]))
                .orElseGet(() -> Collections.singletonList("12345"));

        if (Preferences.check("-b")) {
            final BruteForceScanner bruteForceScanner = new BruteForceScanner();
            int i = 0;
            for (String ip : listSources) {
                log.info("progress: {} {}/{}", ip, ++i, listSources.size());
                bruteForceScanner.brute(ip, listPasswords.toArray(new String[0]));
            }
            if (Preferences.check("-r"))
                IpBruteFilter.cleaning(source.get().split(":")[1]);
        }

        if (Preferences.check("-c")) {
            System.out.println("It can be very long. Please, wait...");
            System.out.println("See log files for more information");

            final CameraScanner scanner = new CameraScanner();
            int c = 0;
            int allAddresses = 0;
            for (String range : listSources) {
                log.info("progress: {} {}/{}", range, ++c, listSources.size());
                int count = scanner.prepareSinglePortScanning(range, 554);
                scanner.scanning();
                allAddresses += count;
            }
            log.info("addresses checked = " + allAddresses);
        }
        System.exit(0);
    }

}