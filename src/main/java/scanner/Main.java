package scanner;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import scanner.http.IpV4AddressRange;
import scanner.http.RangeManager;
import scanner.recover.RecoveryManager;
import scanner.stat.ScanStatGatherer;
import scanner.stat.TimeStatGatherer;
import scanner.stat.TimeStatItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.math.RoundingMode.*;
import static scanner.Preferences.NO_BRUTE;
import static scanner.Preferences.NO_SCANNING;
import static scanner.stat.ScanStatItem.*;

@Slf4j
public class Main {

    public static void main(String[] args) {
        new XReportRunner().make();

        Stopwatch timer = Stopwatch.createStarted();

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
            double expectedTime = BigDecimal.valueOf((0.035d * all) + ((double) all / 100 * 0.5d) * 1.6d).setScale(2, FLOOR).doubleValue();
            TimeStatGatherer.set(TimeStatItem.EXPECTED_TIME, (long) expectedTime * 1000L);

            log.info("addresses will be checked = " + RangeManager.count());
            log.info("expected time: {}", expectedTime);

            for (IpV4AddressRange range : addressCache) {
                log.info("in progress: range {} (ip count = {})", range.toString(), range.size());
                List<String> targetList = new XScanRunner(range).run();

                if (!Preferences.check(NO_BRUTE))
                    new XBruteRunner(targetList, listPasswords).run();

                checked += range.size();
                BigDecimal percent = new BigDecimal((double) checked / all * 100).setScale(2, FLOOR);

                String completePercent = String.format("complete %s/%s (%s%%)", ++c, addressCache.size(), percent);
                log.info(completePercent);
                System.out.println(completePercent);

                ScanStatGatherer.incrementBy(ALL, range.size());
                ScanStatGatherer.increment(RANGES);
                if (range.isLarge())
                    ScanStatGatherer.increment(LARGE_RANGES);
            }
        }

        TimeStatGatherer.set(TimeStatItem.TOTAL_TIME, timer.elapsed(TimeUnit.MILLISECONDS));
        timer.stop();

        new XReportRunner().make();

        RecoveryManager.dropBackup();

        System.exit(0);
    }

}