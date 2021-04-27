package scanner;

import lombok.extern.slf4j.Slf4j;
import scanner.brute.BruteForceScanner;
import scanner.brute.basic.BasicAuthScanner;
import scanner.stat.ReportPrinter;
import scanner.stat.ScreenStatGatherer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Preferences.configure(args);

        String source = Preferences.get("-source");
        String passwords = Preferences.get("-passwords");

        if (source.isEmpty()) {
            log.error("source cannot be empty!");
            System.exit(0);
        }

        List<String> listSources = SourceReader.readSource(source);
        List<String> listPasswords = SourceReader.readSource(passwords);

        if (listPasswords.isEmpty())
            listPasswords.addAll(Collections.singletonList("asdf1234"));

        if (Preferences.check("-b")) {
            final BruteForceScanner bruteForceScanner = new BruteForceScanner();
            int i = 0;
            for (String ip : listSources) {
                log.info("progress: {} {}/{}", ip, ++i, listSources.size());
                bruteForceScanner.brute(ip, listPasswords.toArray(new String[0]));
            }
        }

        if (Preferences.check("-ba")) {
            final BasicAuthScanner basicAuthScanner = new BasicAuthScanner();
            int i = 0;
            for (String ip : listSources) {
                log.info("progress: {} {}/{}", ip, ++i, listSources.size());
                basicAuthScanner.brute(ip, listPasswords.toArray(new String[0]));
            }
        }

        if (Preferences.check("-c")) {
            System.out.println("It can be very long. Please, wait...");
            System.out.println("See log files for more information: /logs/out.log");

            for (String range : listSources)
                CameraRangeManager.prepareSinglePortScanning(range);
            log.info("addresses will be checked = " + CameraRangeManager.count());

            final CameraScanner scanner = new CameraScanner();
            final List<List<InetSocketAddress>> addressCache = CameraRangeManager.getAddressCache();

            int c = 0;
            for (List<InetSocketAddress> listOfIpAddresses : addressCache) {
                log.info("progress: {}/{}", ++c, addressCache.size());
                scanner.scanning(listOfIpAddresses);
            }
        }

        ReportPrinter.print();
        System.exit(0);
    }

}