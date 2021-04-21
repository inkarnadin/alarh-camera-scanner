package scanner.cve;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ConfigurationDecryptTest {

    @Test
    public void parse() {
        Optional<String> parse = ConfigurationDecrypt.parse("\u0000admin\u0000\u0001\u0002#test*\u0000\u0000\u0001\u0002");
        Assert.assertEquals(Optional.of("admin:#test*"), parse);
    }

}