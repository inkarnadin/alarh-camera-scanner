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
import static scanner.stat.ScanStatItem.*;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Preferences.configure(args);
        RecoveryManager.recover();

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
            log.info("addresses will be checked = " + RangeManager.count());

            for (IpV4AddressRange range : addressCache) {
                log.info("in progress: range {} (ip count = {})", range.toString(), range.size());
                List<String> targetList = new XScanRunner(range).run();

                if (!Preferences.check(NO_BRUTE))
                    new XBruteRunner(targetList, listPasswords).run();

                checked += range.size();
                BigDecimal percent = new BigDecimal((double) checked / all * 100).setScale(2, RoundingMode.FLOOR);
                log.info("complete {}/{} ({}%)", ++c, addressCache.size(), percent);

                ScanStatGatherer.incrementBy(ALL, range.size());
                ScanStatGatherer.increment(RANGES);
                if (range.isLarge())
                    ScanStatGatherer.increment(LARGE_RANGES);
            }
        }
        new XReportRunner().run();

        RecoveryManager.dropBackup();

        System.exit(0);
    }

}