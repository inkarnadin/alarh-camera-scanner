package scanner.stat;

import java.math.BigDecimal;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

import static java.math.RoundingMode.*;
import static scanner.stat.ScanStatItem.*;
import static scanner.stat.TimeStatItem.*;

/**
 * Time statistic class.
 *
 * @author inkarnadin
 */
public class TimeStatGatherer {

    private static final Map<TimeStatItem, Double> timeStats = new TreeMap<>() {{
        for (TimeStatItem item : TimeStatItem.values())
            put(item, 0.0d);
    }};

    /**
     * Set certain stats value.
     *
     * @param item stats value
     * @param time explicitly meaning
     */
    public static void set(TimeStatItem item, Long time) {
        timeStats.computeIfPresent(item, (k, v) -> (normalize(v + (double) time / 1000)));
    }

    /**
     * Create text report by gather time data.
     *
     * @return formatted report data
     */
    public static String createReport() {
        recalculate();

        return new StringJoiner("\n")
                .add("Time stats (in second) =============================")
                .add(String.format("Total time: %s s", timeStats.get(TOTAL_TIME)))
                .add(String.format("Expected time: %s s", timeStats.get(EXPECTED_TIME)))
                .add(String.format("Total scanning time: %s s", timeStats.get(TOTAL_SCAN_TIME)))
                .add(String.format("Total brute time: %s s", timeStats.get(TOTAL_BRUTE_TIME)))
                .add(String.format("Average address scanning time: %s s", timeStats.get(AVG_SCAN_TIME)))
                .add(String.format("Average address brute time: %s s", timeStats.get(AVG_BRUTE_TIME)))
                .toString();
    }

    private static void recalculate() {
        timeStats.put(AVG_SCAN_TIME, normalize(timeStats.get(TOTAL_SCAN_TIME) / ScanStatGatherer.get(ALL)));
        timeStats.put(AVG_BRUTE_TIME, normalize(timeStats.get(TOTAL_BRUTE_TIME) / ScanStatGatherer.get(SUCCESS)));
    }

    private static double normalize(double val) {
        return BigDecimal.valueOf(val).setScale(2, UP).doubleValue();
    }

}