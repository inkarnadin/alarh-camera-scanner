package scanner;

import org.junit.Assert;
import org.junit.Test;

public class CameraScannerTest {

    @Test
    public void testPrepareSinglePortScanning() {
        String range = "188.202.63.40-188.202.63.47";

        CameraScanner scanner = new CameraScanner();
        int count = scanner.prepareSinglePortScanning(range);
        Assert.assertEquals(8, count);
    }

}