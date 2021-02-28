package scanner.brute.basic;

import org.junit.Test;

public class BasicAuthScannerTest {

    @Test
    public void brute() {
        BasicAuthScanner ba = new BasicAuthScanner();
        ba.brute("localhost", new String[] { "12345" });
    }

}