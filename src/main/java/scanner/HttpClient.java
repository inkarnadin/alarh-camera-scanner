package scanner;

import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

public class HttpClient {

    private final OkHttpClient client;

    public HttpClient() {
        client = new OkHttpClient()
                .newBuilder()
                .followSslRedirects(true)
                .callTimeout(2, TimeUnit.SECONDS)
                .build();
    }

    @SneakyThrows
    public Response execute(String path) {
        Request request = new Request.Builder()
                .url(path)
                .get().build();
        return client.newCall(request).execute();
    }

}