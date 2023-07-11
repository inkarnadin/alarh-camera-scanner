package scanner.rtsp;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RequestBuilderTest {

    @BeforeClass
    public static void setUp() {
        RTSPContext.set("1.1.1.1", TransportMode.ORTHODOX);
        RTSPContext.set("1.1.1.2", TransportMode.SPECIAL);
    }

    @AfterClass
    public static void tearDown() {
        RTSPContext.evict();
    }

    @Test
    public void build_describe_ortodox() {
        RequestBuilder rbOrtodox = new RequestBuilder("1.1.1.1", "admin:password");
        String describe = rbOrtodox.describe();
        Assert.assertEquals("DESCRIBE rtsp://admin:password@1.1.1.1:554/ RTSP/1.0\r\n" +
                            "CSeq: 1\r\n" +
                            "Content-Type: application/sdp\r\n" +
                            "\r\n", describe);
    }

    @Test
    public void build_describe_special() {
        RequestBuilder rbSpecial = new RequestBuilder("1.1.1.2", "admin:password");
        String describe = rbSpecial.describe();
        Assert.assertEquals("DESCRIBE rtsp://admin:password@1.1.1.2:554/11 RTSP/1.0\r\n" +
                            "CSeq: 1\r\n" +
                            "Content-Type: application/sdp\r\n" +
                            "\r\n", describe);
    }

}