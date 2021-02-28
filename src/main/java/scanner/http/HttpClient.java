package scanner.http;

import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

public class HttpClient {

    private final static OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .followSslRedirects(true)
            .callTimeout(2, TimeUnit.SECONDS)
            .build();

    @SneakyThrows
    public static Response execute(String path) {
        Request request = new Request.Builder()
                .url(path)
                .get().build();
        return client.newCall(request).execute();
    }

    @SneakyThrows
    public static Response executeBasicAuth(String path, String auth) {
        Request request = new Request.Builder()
                .url("http://" + path)
                .addHeader("Authorization", "Basic " + auth)
                .get().build();
        return client.newCall(request).execute();
    }

}