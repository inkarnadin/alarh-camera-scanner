package scanner.runner;

import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.http.ip.IpV4AddressRange;
import scanner.http.ip.RangeUtils;
import scanner.stat.StatsUtility;

import java.util.Set;

import static scanner.Preferences.NO_BRUTE;
import static scanner.Preferences.NO_SCANNING;

/**
 * Class for start brute and scan processes.
 *
 * @author inkarnadin
 * on 07-08-2022
 */
@Slf4j
@Deprecated
public class MainRunner {

    BruteRunner bruteRunner = new BruteRunner();
    ScanRunner scanRunner = new ScanRunner();

    /**
     * Method fof starting brute and scan processes.
     */
    public void run() {
        Set<String> listSources = Preferences.getRangesList();
        Set<String> listPasswords = Preferences.getPasswordsList();

        Set<IpV4AddressRange> ranges = RangeUtils.prepare(listSources);

        int completeRange = 0;
        long completeAddress = 0;
        long totalAddress = RangeUtils.count(ranges);
        log.info("total addresses to scanning: {}", totalAddress);

        boolean needScanning = !Preferences.check(NO_SCANNING);
        boolean needBrute = !Preferences.check(NO_BRUTE);

        if (needScanning) {
            StatsUtility.calculateTotalTime(totalAddress);
            for (IpV4AddressRange range : ranges) {
                log.info("in progress: range {} (ip count = {})", range.getSourceRange(), range.getCount());
                Set<String> targetList = scanRunner.run(range);

                if (needBrute) {
                    bruteRunner.run(targetList, listPasswords);
                }

                completeAddress += range.getCount();
                StatsUtility.calculateRemindTime(totalAddress, completeAddress, ++completeRange, ranges.size(), range);
            }
        } else {
            if (needBrute) {
                log.info("no checking start, all ip from list will be brute without check");
                bruteRunner.run(listSources, listPasswords);
            }
        }
    }

}