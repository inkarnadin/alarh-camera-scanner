package scanner.stat;

import org.junit.Assert;
import org.junit.Test;

import static scanner.stat.ScanStatItem.*;
import static scanner.stat.StatDataHolder.SCAN_GATHERER;

public class ScanStatGathererTest {

    @Test
    public void get_stats_as_string_success() {
        SCAN_GATHERER.set(TOTAL, 10L);
        SCAN_GATHERER.increment(SUCCESS);
        SCAN_GATHERER.increment(SUCCESS);
        SCAN_GATHERER.increment(FAILURE);
        SCAN_GATHERER.increment(LARGE_RANGES);
        SCAN_GATHERER.increment(LARGE_RANGES);
        SCAN_GATHERER.increment(LARGE_RANGES);

        Assert.assertEquals("10;0;3;2;8", SCAN_GATHERER.getStatsAsString());
    }

}