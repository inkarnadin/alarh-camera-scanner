package scanner.stat;

import java.util.stream.Collectors;

import static scanner.stat.ScanStatItem.*;

/**
 * Scan save statistic class.
 *
 * @author inkarnadin
 */
public class ScanStatGatherer extends AbstractStatGatherer<ScanStatItem, Long> {

    public ScanStatGatherer() {
        data.put(TOTAL, 0L);

        data.put(RANGES, 0L);
        data.put(LARGE_RANGES, 0L);

        data.put(SUCCESS, 0L);
        data.put(FAILURE, 0L);
    }

    /**
     * Get certain value.
     *
     * @param item stats value
     * @return value by key
     */
    @Override
    public Long get(ScanStatItem item) {
        return data.getOrDefault(item, 0L);
    }

    /**
     * Increment certain stats value.
     *
     * @param item stats value
     */
    @Override
    public void increment(ScanStatItem item) {
        data.computeIfPresent(item, (x, y) -> ++y);
    }

    /**
     * Increment by value.
     *
     * @param item stats value
     * @param value increase by value
     */
    @Override
    public void incrementBy(ScanStatItem item, Long value) {
        data.computeIfPresent(item, (x, y) -> (y + value));
    }

    /**
     * Get all statistic values in natural ordered with splitter.
     *
     * @return all values as string
     */
    public String getStatsAsString() {
        recalculate();
        return data.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(";"));
    }

    @Override
    protected void recalculate() {
        data.put(FAILURE, data.get(TOTAL) - data.get(SUCCESS));
    }

}