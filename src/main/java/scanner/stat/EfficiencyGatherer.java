package scanner.stat;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static scanner.stat.EfficiencyItem.*;
import static scanner.stat.StatDataHolder.SCAN_GATHERER;
import static scanner.stat.StatDataHolder.SCREEN_GATHERER;

/**
 * Efficiency statistic calculate class.
 *
 * @author inkarnadin
 */
public class EfficiencyGatherer extends AbstractStatGatherer<EfficiencyItem, Double> {

    public EfficiencyGatherer() {
        data.put(SCAN_EFFICIENCY, 0.0d);
        data.put(BRUTE_EFFICIENCY, 0.0d);
        data.put(GET_FRAME_EFFICIENCY, 0.0d);
    }

    protected void recalculate() {
        if (SCAN_GATHERER.get(ScanStatItem.ALL) > 0) {
            double scanEff = (double) SCAN_GATHERER.get(ScanStatItem.SUCCESS) / SCAN_GATHERER.get(ScanStatItem.ALL) * 100;
            data.put(SCAN_EFFICIENCY, BigDecimal.valueOf(scanEff).setScale(2, RoundingMode.FLOOR).doubleValue());
        }

        if (SCAN_GATHERER.get(ScanStatItem.SUCCESS) > 0) {
            double bruteEff = (double) SCREEN_GATHERER.get(ScreenStatItem.ALL) / SCAN_GATHERER.get(ScanStatItem.SUCCESS) * 100;
            data.put(BRUTE_EFFICIENCY, BigDecimal.valueOf(bruteEff).setScale(2, RoundingMode.FLOOR).doubleValue());
        }

        if (SCREEN_GATHERER.get(ScreenStatItem.ALL) > 0) {
            double getFrameEff = (double) SCREEN_GATHERER.get(ScreenStatItem.SUCCESS) / SCREEN_GATHERER.get(ScreenStatItem.ALL) * 100;
            data.put(GET_FRAME_EFFICIENCY, BigDecimal.valueOf(getFrameEff).setScale(2, RoundingMode.FLOOR).doubleValue());
        }
    }

}