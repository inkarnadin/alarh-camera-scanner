package scanner;

import org.junit.Assert;
import org.junit.Test;
import scanner.http.RangeManager;

public class CameraScannerTest {

    @Test
    public void testPrepareSinglePortScanning() {
        String range = "188.202.63.40-188.202.63.47";

        RangeManager.prepare(range);
        Assert.assertEquals(8, RangeManager.count());
    }

}