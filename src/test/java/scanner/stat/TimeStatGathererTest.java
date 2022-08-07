package scanner.stat;

import org.junit.Assert;
import org.junit.Test;

import static scanner.stat.TimeStatItem.TOTAL_TIME;

public class TimeStatGathererTest {

    @Test
    public void get_time_from_mills() {
        TimeStatGatherer gatherer = new TimeStatGatherer();
        gatherer.set(TOTAL_TIME, 5000000005L);

        Assert.assertEquals("1388:53:20.005", gatherer.getData().get(TOTAL_TIME.toString()));
    }

}