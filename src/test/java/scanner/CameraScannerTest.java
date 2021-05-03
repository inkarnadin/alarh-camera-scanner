package scanner;

import org.junit.Assert;
import org.junit.Test;
import scanner.scan.CameraScanRangeManager;

public class CameraScannerTest {

    @Test
    public void testPrepareSinglePortScanning() {
        String range = "188.202.63.40-188.202.63.47";

        CameraScanRangeManager.prepareSinglePortScanning(range);
        Assert.assertEquals(8, CameraScanRangeManager.count());
    }

}