package scanner.recover;

import org.junit.Assert;
import org.junit.Test;

import static scanner.recover.RecoveryElement.SOURCE_CHECKSUM;

public class RecoveryElementTest {

    @Test
    public void find_success() {
        RecoveryElement sc = RecoveryElement.find("sourceChecksum");
        Assert.assertEquals(SOURCE_CHECKSUM, sc);
    }

    @Test
    public void find_failure() {
        RecoveryElement sc = RecoveryElement.find("unknown");
        Assert.assertNull(sc);
    }

}