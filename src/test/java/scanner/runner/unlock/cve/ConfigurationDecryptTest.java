package scanner.runner.unlock.cve;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ConfigurationDecryptTest {

    @Test
    public void parse() {
        Optional<String> parsed = ConfigurationDecrypt.parse("\u0000admin\u0000\u0001\u0002#test*\u0000\u0000\u0001\u0002");
        Assert.assertEquals(Optional.of("admin:#test*"), parsed);
    }

    @Test
    public void parse_not_found() {
        Optional<String> parsed = ConfigurationDecrypt.parse("\u0000un\u0000\u0001\u0002pn\u0000\u0000\u0001\u0002");
        Assert.assertTrue(parsed.isEmpty());
    }

}