package scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.analyze.ProblemResolver;
import scanner.brute.BruteForceScanner;
import scanner.stat.TimeStatGatherer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static scanner.stat.TimeStatItem.*;

/**
 * Find password camera logic class.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class XBruteRunner extends AbstractRunner {

    private final List<String> listSources;
    private final List<String> listPasswords;

    /**
     * Execute find password (brute, cve and other).
     */
    public void run() {
        try {
            final BruteForceScanner bruteForceScanner = new BruteForceScanner();
            int i = 0;
            for (String ip : listSources) {
                bruteForceScanner.brute(ip, listPasswords.toArray(new String[0]));

                BigDecimal percent = BigDecimal.valueOf((double) ++i / listSources.size() * 100).setScale(2, RoundingMode.FLOOR);
                log.info("subtask complete {}/{} ({}%): {}", i, listSources.size(), percent, ip);
            }
            ProblemResolver.run();
        } catch (Exception xep) {
            log.error("Error during brute attack: {}", xep.getMessage());
        }
        TimeStatGatherer.set(TOTAL_BRUTE_TIME, timer.elapsed(TimeUnit.MILLISECONDS));
        timer.stop();
    }

}