package scanner.stat;

import com.google.common.base.Strings;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import static scanner.stat.ScanStatItem.ALL;
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

                            LocalTime time = LocalTime.ofSecondOfDay(timeInSeconds);

                            String msAsString = Strings.padEnd(Long.toString(clearMilliseconds), 3, '0');
                            String timeAsString = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                            return String.format("%s.%s", timeAsString, msAsString);
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
        if (SCAN_GATHERER.get(ALL) > 0)
            data.put(AVG_SCAN_TIME, data.get(TOTAL_SCAN_TIME) / SCAN_GATHERER.get(ALL));
        if (SCAN_GATHERER.get(SUCCESS) > 0)
            data.put(AVG_BRUTE_TIME, data.get(TOTAL_BRUTE_TIME) / SCAN_GATHERER.get(SUCCESS));
    }

}