package scanner.stat;

import org.junit.Assert;
import org.junit.Test;

import static scanner.stat.ScanStatEnum.*;
import static scanner.stat.ScanStatEnum.SUCCESS;

public class ScanStatGathererTest {

    @Test
    public void createReport_success() {
        ScanStatGatherer.set(ALL, 3);
        ScanStatGatherer.set(RANGES, 1);
        ScanStatGatherer.set(LARGE_RANGES, 1);
        ScanStatGatherer.set(SUCCESS, 2);
        ScanStatGatherer.set(FAILURE, 1);

        String report = "Scanning stats =============================\n" +
                        "All ip scanned: 3\n" +
                        "Scanned range: 1\n" +
                        "Scanned large range: 1\n" +
                        "Success scanned: 2\n" +
                        "Failure scanned: 1";

        Assert.assertEquals(report, ScanStatGatherer.createReport());
    }

    @Test
    public void getStatsAsString_success() {
        ScanStatGatherer.set(ALL, 10);
        ScanStatGatherer.increment(SUCCESS);
        ScanStatGatherer.increment(SUCCESS);
        ScanStatGatherer.increment(FAILURE);
        ScanStatGatherer.increment(LARGE_RANGES);
        ScanStatGatherer.increment(LARGE_RANGES);
        ScanStatGatherer.increment(LARGE_RANGES);

        Assert.assertEquals("10;0;3;2;8", ScanStatGatherer.getStatsAsString());
    }

}