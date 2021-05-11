package scanner;

import org.junit.Assert;
import org.junit.Test;

import static scanner.Preferences.PASSWORD_PATH;
import static scanner.Preferences.RANGE_PATH;

public class PreferencesTest {

    static {
        Preferences.configure(new String[] {"-exists_flag", "-source:list.txt"});
    }

    @Test
    public void testCheckPreferences() {
        Boolean rCheck = Preferences.check("-exists_flag");
        Assert.assertEquals(true, rCheck);

        Boolean dCheck = Preferences.check("-not_exists_flag");
        Assert.assertEquals(false, dCheck);
    }

    @Test
    public void testGetPreferences() {
        String sources = Preferences.get(RANGE_PATH);
        Assert.assertEquals("list.txt", sources);

        String passwords = Preferences.get(PASSWORD_PATH);
        Assert.assertNull(passwords);
    }

}