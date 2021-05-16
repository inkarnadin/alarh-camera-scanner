package scanner.stat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

import static scanner.stat.EfficiencyItem.*;

/**
 * Efficiency statistic calculate class.
 *
 * @author inkarnadin
 */
public class EfficiencyGatherer {

    private static final Map<EfficiencyItem, Double> efficiencyMap = new TreeMap<>() {{
        put(SCAN_EFFICIENCY, 0.0d);
        put(BRUTE_EFFICIENCY, 0.0d);
        put(GET_FRAME_EFFICIENCY, 0.0d);
    }};

    /**
     * Create report by efficiency statistic data.
     *
     * @return formatted report data
     */
    public static String createReport() {
        recalculate();

        return new StringJoiner("\n")
                .add("Efficiency stats =============================")
                .add(String.format("Scanning: %s%%", efficiencyMap.get(SCAN_EFFICIENCY)))
                .add(String.format("Brute: %s%%", efficiencyMap.get(BRUTE_EFFICIENCY)))
                .add(String.format("Get frame: %s%%", efficiencyMap.get(GET_FRAME_EFFICIENCY)))
                .toString();
    }

    private static void recalculate() {
        double scanEff = (double) ScanStatGatherer.get(ScanStatItem.SUCCESS) / ScanStatGatherer.get(ScanStatItem.ALL) * 100;
        efficiencyMap.put(SCAN_EFFICIENCY, BigDecimal.valueOf(scanEff).setScale(2, RoundingMode.FLOOR).doubleValue());

        double bruteEff = (double) ScreenStatGatherer.get(ScreenStatItem.ALL) / ScanStatGatherer.get(ScanStatItem.SUCCESS) * 100;
        efficiencyMap.put(BRUTE_EFFICIENCY, BigDecimal.valueOf(bruteEff).setScale(2, RoundingMode.FLOOR).doubleValue());

        double getFrameEff = (double) ScreenStatGatherer.get(ScreenStatItem.SUCCESS) / ScreenStatGatherer.get(ScreenStatItem.ALL) * 100;
        efficiencyMap.put(GET_FRAME_EFFICIENCY, BigDecimal.valueOf(getFrameEff).setScale(2, RoundingMode.FLOOR).doubleValue());
    }

}