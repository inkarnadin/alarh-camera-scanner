package scanner;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class PreferencesTest {

    static {
        Preferences.configure(new String[] {"-r", "-source:list.txt"});
    }

    @Test
    public void testCheckPreferences() {
        Boolean rCheck = Preferences.check("-r");
        Assert.assertEquals(true, rCheck);

        Boolean dCheck = Preferences.check("-d");
        Assert.assertEquals(false, dCheck);
    }

    @Test
    public void testGetPreferences() {
        Optional<String> sources = Preferences.get("-source");
        Assert.assertEquals(Optional.of("list.txt"), sources);

        Optional<String> passwords = Preferences.get("-passwords");
        Assert.assertEquals(Optional.empty(), passwords);
    }


}