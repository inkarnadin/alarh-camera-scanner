package scanner.stat;

import lombok.extern.slf4j.Slf4j;
import scanner.http.ip.IpV4AddressRange;

import java.math.BigDecimal;

import static java.math.RoundingMode.FLOOR;
import static scanner.stat.ScanStatItem.*;
import static scanner.stat.StatDataHolder.SCAN_GATHERER;
import static scanner.stat.StatDataHolder.TIME_GATHERER;

/**
 * Stats utility class.
 *
 * @author inkarnadin
 * on 07-08-2022
 */
@Slf4j
@Deprecated
public final class StatsUtility {

    private StatsUtility() {
        throw new AssertionError("Utility class can't be instantiate");
    }

    private static final double avgOneAddressScanCoeff = 0.035d;
    private static final double avgOneAddressBruteCoeff = 0.5d;

    /**
     * Method calculate total time of scanning.
     *
     * @param addressCount count of addresses
     */
    public static void calculateTotalTime(long addressCount) {
        double calculatedTime = (avgOneAddressScanCoeff * addressCount) + (addressCount * avgOneAddressBruteCoeff) * 1000;
        long expectedTime = Double.valueOf(calculatedTime).longValue();
        TIME_GATHERER.set(TimeStatItem.EXPECTED_TIME, expectedTime);

        log.info("addresses will be checked = " + addressCount);
        log.info("expected time: {}", expectedTime);
    }

    /**
     * Method calculate remind time of scanning.
     *
     * @param total total address count
     * @param completeAddress completed scan addresses
     * @param completeRange completed scan ranges
     * @param rangeCount count of range
     * @param range current range
     */
    public static void calculateRemindTime(long total,
                                           long completeAddress,
                                           long completeRange,
                                           long rangeCount,
                                           IpV4AddressRange range) {
        BigDecimal percent = new BigDecimal((double) completeAddress / total * 100).setScale(2, FLOOR);

        String completePercent = String.format("complete %s/%s (%s%%)", completeRange, range.getCount(), percent);
        log.info(completePercent);
        System.out.println(completePercent);

        SCAN_GATHERER.incrementBy(TOTAL, rangeCount);
        SCAN_GATHERER.increment(RANGES);
        if (range.isLarge()) {
            SCAN_GATHERER.increment(LARGE_RANGES);
        }
    }

}