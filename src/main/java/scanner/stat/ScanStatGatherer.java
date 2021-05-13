package scanner.stat;

import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static scanner.stat.ScanStatEnum.*;

/**
 * Scan save statistic class.
 *
 * @author inkarnadin
 */
public class ScanStatGatherer {

    private static final Map<ScanStatEnum, Long> scanStats = new TreeMap<>() {{
        put(ALL, 0L);

        put(RANGES, 0L);
        put(LARGE_RANGES, 0L);

        put(SUCCESS, 0L);
        put(FAILURE, 0L);
    }};

    /**
     * Create full report by gather statistic data.
     *
     * @return formatted report data
     */
    public static String createReport() {
        recalculate();

        return new StringJoiner("\n")
                .add("Scanning stats =============================")
                .add(String.format("All ip scanned: %s", scanStats.get(ALL)))
                .add(String.format("Scanned range: %s", scanStats.get(RANGES)))
                .add(String.format("Scanned large range: %s", scanStats.get(LARGE_RANGES)))
                .add(String.format("Success scanned: %s", scanStats.get(SUCCESS)))
                .add(String.format("Failure scanned: %s", scanStats.get(FAILURE)))
                .toString();
    }

    /**
     * Set certain stats value.
     *
     * @param item stats value
     * @param value explicitly meaning
     */
    public static void set(ScanStatEnum item, long value) {
        scanStats.put(item, value);
    }

    /**
     * Increment certain stats value.
     *
     * @param item stats value
     */
    public static void increment(ScanStatEnum item) {
        scanStats.computeIfPresent(item, (x, y) -> ++y);
    }

    /**
     * Get all statistic values in natural ordered with splitter.
     *
     * @return all values as string
     */
    public static String getStatsAsString() {
        recalculate();
        return scanStats.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(";"));
    }

    private static void recalculate() {
        scanStats.put(FAILURE, scanStats.get(ALL) - scanStats.get(SUCCESS));
    }

}