package scanner.runner;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.analyze.ProblemResolver;
import scanner.brute.BruteForceScanner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static scanner.Preferences.ALLOW_FRAME_SAVING;
import static scanner.stat.StatDataHolder.TIME_GATHERER;
import static scanner.stat.TimeStatItem.TOTAL_BRUTE_TIME;

/**
 * Find password camera logic class.
 *
 * @author inkarnadin
 */
@Slf4j
@NoArgsConstructor
@Deprecated
public class BruteRunner extends AbstractRunner {

    /**
     * Execute find password (brute, cve and other).
     *
     * The incoming list of targets is checked. Next, the range is checked for those
     * password values that were obtained through vulnerabilities on some devices. The same
     * passwords can be set on other devices that are not affected by the vulnerability being tested.
     *
     * During the check, the data is entered into the appropriate lists. In addition,
     * when the corresponding flag is selected, screenshots from the available targets will be saved.
     *
     * After all the checks, an additional attempt is made to save screenshots at those addresses for which,
     * for some reason, they were not received and these addresses were added to the list of known and resolved
     * problems.
     */
    public void run(Set<String> listSources, Set<String> listPasswords) {
        if (!timer.isRunning()) {
            timer.start();
        }

        try {
            final BruteForceScanner bruteForceScanner = new BruteForceScanner();
            int i = 0;
            for (String ip : listSources) {
                bruteForceScanner.brute(ip, listPasswords.toArray(new String[0]));

                BigDecimal percent = BigDecimal.valueOf((double) ++i / listSources.size() * 100).setScale(2, RoundingMode.FLOOR);
                log.info("subtask complete {}/{} ({}%): {}", i, listSources.size(), percent, ip);
            }

            String[] passwords = bruteForceScanner.getCheckCVEContainer().getAdditionalPasswords().toArray(new String[0]);
            if (passwords.length > 0) {
                log.info("check additional passwords {}", Arrays.asList(passwords));
                for (String ip : bruteForceScanner.getCheckCVEContainer().updateAddressList(listSources)) {
                    bruteForceScanner.brute(ip, passwords);
                }
            }

            if (Preferences.check(ALLOW_FRAME_SAVING))
                ProblemResolver.run();
        } catch (Exception xep) {
            log.error("error during brute attack: {}", xep.getMessage());
        }
        TIME_GATHERER.set(TOTAL_BRUTE_TIME, timer.elapsed(TimeUnit.MILLISECONDS));
        timer.stop();
    }

}