package scanner.rtsp;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ResponseHandlerTest {

    @Test
    @SneakyThrows
    public void handle_digest_auth() {
        String template = "RTSP/1.0 200 OK\n" +
                          "CSeq: 2\n" +
                          "Date: Fri, Apr 23 2018 10:54:00 GMT\n" +
                          "WWW-Authenticate: Digest realm=Streaming Server, nonce=7ccc1\n" +
                          "Session: 621841\n";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8));
        InputStreamReader reader = new InputStreamReader(byteArrayInputStream);
        try (BufferedReader br = new BufferedReader(reader)) {
            RTSPResponse response = ResponseHandler.handle(br);
            Assert.assertEquals("RTSP/1.0 200 OK", response.getStatusLine());
            Assert.assertEquals(200, response.getCode());
            Assert.assertEquals("OK", response.getCodeDescription());
            Assert.assertEquals(2, response.getCSeq());
            Assert.assertEquals("Fri, Apr 23 2018 10:54:00 GMT", response.getDateAsString());
            Assert.assertEquals("Digest realm=Streaming Server, nonce=7ccc1", response.getDigestAuth());
            Assert.assertNull(response.getBasicAuth());
            Assert.assertEquals("621841", response.getSession());
        }
    }

    @Test
    @SneakyThrows
    public void handle_basic_auth() {
        String template = "RTSP/1.0 200 OK\n" +
                          "CSeq: 0\n" +
                          "Date: Fri, Apr 25 2018 10:54:00 GMT\n" +
                          "WWW-Authenticate: Basic realm=Streaming Server, nonce=76bf1\n" +
                          "Session: 184556\n";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8));
        InputStreamReader reader = new InputStreamReader(byteArrayInputStream);
        try (BufferedReader br = new BufferedReader(reader)) {
            RTSPResponse response = ResponseHandler.handle(br);
            Assert.assertEquals("RTSP/1.0 200 OK", response.getStatusLine());
            Assert.assertEquals(200, response.getCode());
            Assert.assertEquals("OK", response.getCodeDescription());
            Assert.assertEquals(0, response.getCSeq());
            Assert.assertEquals("Fri, Apr 25 2018 10:54:00 GMT", response.getDateAsString());
            Assert.assertNull(response.getDigestAuth());
            Assert.assertEquals("Basic realm=Streaming Server, nonce=76bf1", response.getBasicAuth());
            Assert.assertEquals("184556", response.getSession());
        }
    }

    @Test
    @SneakyThrows
    public void handle_wrong_response() {
        String template = "WRONG";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8));
        InputStreamReader reader = new InputStreamReader(byteArrayInputStream);
        try (BufferedReader br = new BufferedReader(reader)) {
            RTSPResponse response = ResponseHandler.handle(br);
            Assert.assertEquals(418, response.getCode());
        }
    }

    @SneakyThrows
    @Test(expected = IOException.class)
    public void handle_throw_exception() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        InputStreamReader reader = new InputStreamReader(byteArrayInputStream);
        try (BufferedReader br = new BufferedReader(reader)) {
            br.close();
            ResponseHandler.handle(br);
        }
    }

    @Test
    @SneakyThrows
    public void handle_interrupted_by_empty_line() {
        String template = "RTSP/1.0 200 OK\n\n\n\n\n";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8));
        InputStreamReader reader = new InputStreamReader(byteArrayInputStream);
        try (BufferedReader br = new BufferedReader(reader)) {
            RTSPResponse response = ResponseHandler.handle(br);
            Assert.assertEquals(200, response.getCode());
        }
    }

}