package scanner.stat;

import org.junit.Assert;
import org.junit.Test;

import static scanner.stat.ScreenStatItem.ALL;
import static scanner.stat.ScreenStatItem.WRONG_AUTH_ERROR;
import static scanner.stat.StatDataHolder.SCREEN_GATHERER;

public class ScreenStatGathererTest {

    @Test
    public void get_stats_as_string_success() {
        SCREEN_GATHERER.increment(ALL);
        SCREEN_GATHERER.increment(ALL);
        SCREEN_GATHERER.increment(WRONG_AUTH_ERROR);

        Assert.assertEquals("2;0;2;0;1;0;0;0;0;0;0;1", SCREEN_GATHERER.getStatsAsString());
    }

}