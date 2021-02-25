import org.junit.Test;
import scanner.brute.BruteForceScanner;

public class NewBruteTest {

    @Test
    public void test() {
        BruteForceScanner newBruteScanner = new BruteForceScanner();
        newBruteScanner.brute("176.115.122.19", new String[] { "12345678q", "123456", "asdf1234" });
    }

}