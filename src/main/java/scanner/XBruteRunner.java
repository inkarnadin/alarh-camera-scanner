package scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.analyze.ProblemResolver;
import scanner.brute.BruteForceScanner;

import java.util.List;

/**
 * Find password camera logic class.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class XBruteRunner implements Runner {

    private final List<String> listSources;
    private final List<String> listPasswords;

    /**
     * Execute find password (brute, cve and other) if set {@code -b} flag.
     */
    @Override
    public void run() {
        try {
            if (Preferences.check("-b")) {
                final BruteForceScanner bruteForceScanner = new BruteForceScanner();
                int i = 0;
                for (String ip : listSources) {
                    log.info("progress: {} {}/{}", ip, ++i, listSources.size());
                    bruteForceScanner.brute(ip, listPasswords.toArray(new String[0]));
                }
                ProblemResolver.run();
            }
        } catch (Exception xep) {
            log.error("Error during brute attack: {}", xep.getMessage());
        }
    }

}