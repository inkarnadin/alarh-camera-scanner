package scanner;

import lombok.extern.slf4j.Slf4j;
import scanner.http.IpV4AddressRange;
import scanner.http.RangeManager;
import scanner.recover.RecoveryManager;
import scanner.stat.ScanStatGatherer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static scanner.Preferences.NO_BRUTE;
import static scanner.Preferences.NO_SCANNING;
import static scanner.stat.ScanStatEnum.ALL;
import static scanner.stat.ScanStatEnum.RANGES;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Preferences.configure(args);

        RecoveryManager.recover();
        RecoveryManager.initNewRestoreProcess();

        List<String> listRanges = Preferences.getRangesList();
        List<String> listPasswords = Preferences.getPasswordsList();

        for (String range : listRanges)
            RangeManager.prepare(range);

        final List<IpV4AddressRange> addressCache = RangeManager.getAddressCache();

        int c = 0;
        long checked = 0;
        long all = RangeManager.count();

        if (Preferences.check(NO_SCANNING)) {
            if (!Preferences.check(NO_BRUTE)) {
                log.info("No checking start. All ip from list will be brute without check");
                new XBruteRunner(listRanges, listPasswords).run();
            }
        } else {
            ScanStatGatherer.set(ALL, RangeManager.count());
            ScanStatGatherer.set(RANGES, listRanges.size());
            log.info("addresses will be checked = " + RangeManager.count());

            for (IpV4AddressRange range : addressCache) {
                List<String> targetList = new XScanRunner(range).run();

                if (!Preferences.check(NO_BRUTE))
                    new XBruteRunner(targetList, listPasswords).run();

                checked += range.size();
                BigDecimal percent = new BigDecimal((double) checked / all * 100).setScale(2, RoundingMode.FLOOR);
                log.info("complete {}/{} ({}%): range = {} (ip count = {})", ++c, addressCache.size(), percent, range.toString(), range.size());
            }
        }
        new XReportRunner().run();

        System.exit(0);
    }

}