package scanner;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class SourceReaderTest {

    @Test
    public void readSource() {
        Set<String> addresses = SourceReader.readSource("src/test/resources/duplicates.txt");
        Assert.assertEquals(Set.of("109.195.129.87", "109.195.128.133"), addresses);
    }

    @Test
    public void checksum() {
        String checksum = SourceReader.checksum("src/test/resources/duplicates.txt");
        Assert.assertEquals("80781CEFABF9AA04881FD137A05FB216", checksum);
    }

}