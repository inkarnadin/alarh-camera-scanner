package scanner.stat;

import org.junit.Assert;
import org.junit.Test;

import static scanner.stat.ScreenStatItem.*;

public class ScreenStatGathererTest {

    @Test
    public void getStatsAsString_success() {
        ScreenStatGatherer.increment(ALL);
        ScreenStatGatherer.increment(ALL);
        ScreenStatGatherer.increment(WRONG_AUTH_ERROR);

        Assert.assertEquals("2;0;2;0;1;0;0;0;0;0;0;1", ScreenStatGatherer.getStatsAsString());
    }

}