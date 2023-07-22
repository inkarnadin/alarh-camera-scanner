package scanner.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import lombok.SneakyThrows;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class HttpClientTest {

    static WireMockServer wireMockServer = new WireMockServer(Options.DEFAULT_PORT);

    @BeforeClass
    public static void setUp() {
        wireMockServer.start();
        stubFor(get(urlEqualTo("/doGetSuccess")));
        stubFor(put(urlEqualTo("/doPutSuccess")));
    }

    @AfterClass
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @SneakyThrows
    public void do_get_success() {
        Response response = HttpClient.doGet("http://localhost:8080/doGetSuccess");
        Assert.assertEquals(200, response.code());
    }

    @Test
    @SneakyThrows
    public void do_put_success() {
        Response response = HttpClient.doPut(
                "http://localhost:8080/doPutSuccess",
                RequestBody.create("testBody".getBytes(StandardCharsets.UTF_8))
        );
        Assert.assertEquals(200, response.code());
    }

}