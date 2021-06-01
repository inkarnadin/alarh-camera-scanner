package scanner.stat;

import java.math.BigDecimal;

import static java.math.RoundingMode.UP;
import static scanner.stat.ScanStatItem.ALL;
import static scanner.stat.ScanStatItem.SUCCESS;
import static scanner.stat.StatDataHolder.SCAN_GATHERER;
import static scanner.stat.TimeStatItem.*;

/**
 * Time statistic class.
 *
 * @author inkarnadin
 */
public class TimeStatGatherer extends AbstractStatGatherer<TimeStatItem, Double> {

    public TimeStatGatherer() {
        for (TimeStatItem item : TimeStatItem.values())
            data.put(item, 0.0d);
    }

    /**
     * Set certain stats value.
     *
     * @param item stats value
     * @param time explicitly meaning
     */
    @Override
    public void set(TimeStatItem item, Double time) {
        data.computeIfPresent(item, (k, v) -> (normalize(v + time / 1000)));
    }

    protected void recalculate() {
        if (SCAN_GATHERER.get(ALL) > 0)
            data.put(AVG_SCAN_TIME, normalize(data.get(TOTAL_SCAN_TIME) / SCAN_GATHERER.get(ALL)));
        if (SCAN_GATHERER.get(SUCCESS) > 0)
            data.put(AVG_BRUTE_TIME, normalize(data.get(TOTAL_BRUTE_TIME) / SCAN_GATHERER.get(SUCCESS)));
    }

    private static double normalize(double val) {
        return BigDecimal.valueOf(val).setScale(2, UP).doubleValue();
    }

}