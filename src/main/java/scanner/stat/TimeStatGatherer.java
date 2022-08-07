package scanner.stat;

import java.util.Map;
import java.util.stream.Collectors;

import static scanner.stat.ScanStatItem.TOTAL;
import static scanner.stat.ScanStatItem.SUCCESS;
import static scanner.stat.StatDataHolder.SCAN_GATHERER;
import static scanner.stat.TimeStatItem.*;

/**
 * Time statistic class.
 *
 * @author inkarnadin
 */
public class TimeStatGatherer extends AbstractStatGatherer<TimeStatItem, Long> {

    public TimeStatGatherer() {
        for (TimeStatItem item : TimeStatItem.values())
            data.put(item, 0L);
    }

    /**
     * Get statistic data as key-value pairs.
     *
     * @return map of statistic values
     */
    @Override
    public Map<String, String> getData() {
        recalculate();

        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> {
                            long clearMilliseconds = entry.getValue() % 1000;
                            long timeInSeconds = entry.getValue() / 1000;

                            long hours = timeInSeconds / 3600;
                            long minutes = (timeInSeconds % 3600) / 60;
                            long seconds = timeInSeconds % 60;

                            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, clearMilliseconds);
                        })
                );
    }

    /**
     * Set certain stats value.
     *
     * @param item stats value
     * @param time explicitly meaning
     */
    @Override
    public void set(TimeStatItem item, Long time) {
        data.computeIfPresent(item, (k, v) -> (v + time));
    }

    protected void recalculate() {
        if (SCAN_GATHERER.get(TOTAL) > 0)
            data.put(AVG_SCAN_TIME, data.get(TOTAL_SCAN_TIME) / SCAN_GATHERER.get(TOTAL));
        if (SCAN_GATHERER.get(SUCCESS) > 0)
            data.put(AVG_BRUTE_TIME, data.get(TOTAL_BRUTE_TIME) / SCAN_GATHERER.get(SUCCESS));
    }

}