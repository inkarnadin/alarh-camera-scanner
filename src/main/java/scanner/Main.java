package scanner;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import scanner.http.IpV4AddressRange;
import scanner.http.RangeManager;
import scanner.recover.RecoveryManager;
import scanner.runner.XBruteRunner;
import scanner.runner.XReportRunner;
import scanner.runner.XScanRunner;
import scanner.stat.TimeStatItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.math.RoundingMode.FLOOR;
import static scanner.Preferences.NO_BRUTE;
import static scanner.Preferences.NO_SCANNING;
import static scanner.stat.ScanStatItem.*;
import static scanner.stat.StatDataHolder.SCAN_GATHERER;
import static scanner.stat.StatDataHolder.TIME_GATHERER;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Stopwatch timer = Stopwatch.createStarted();

        Preferences.configure(args);
        RecoveryManager.recover();

        List<String> listRanges = Preferences.getRangesList();
        List<String> listPasswords = Preferences.getPasswordsList();

        RangeManager.validateMaxAddressesCount(listRanges);

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
            Long expectedTime = ExpectedTimeCalculator.expectedTime(all);
            TIME_GATHERER.set(TimeStatItem.EXPECTED_TIME, expectedTime);

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

                SCAN_GATHERER.incrementBy(ALL, (long) range.size());
                SCAN_GATHERER.increment(RANGES);
                if (range.isLarge())
                    SCAN_GATHERER.increment(LARGE_RANGES);
            }
        }

        TIME_GATHERER.set(TimeStatItem.TOTAL_TIME, timer.elapsed(TimeUnit.MILLISECONDS));
        timer.stop();

        new XReportRunner().run();

        RecoveryManager.dropBackup();

        System.exit(0);
    }

}