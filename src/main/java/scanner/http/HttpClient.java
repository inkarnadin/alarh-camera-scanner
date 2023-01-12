package scanner.http;

import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

/**
 * Класс, реализующий веб-клиент для работы с rest-методами.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
public class HttpClient {

    private final static OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .followSslRedirects(true)
            .callTimeout(500, TimeUnit.MILLISECONDS)
            .build();

    /**
     * Метод выполнения <b>GET</b> запроса.
     *
     * @param path путь
     * @return ответ
     */
    @SneakyThrows
    public static Response doGet(String path) {
        Request request = new Request.Builder()
                .url(path)
                .get().build();
        return client.newCall(request).execute();
    }

    /**
     * Метод выполнения <b>PUT</b> запроса.
     *
     * @param path путь
     * @param body тело запроса
     * @return ответ
     */
    @SneakyThrows
    public static Response doPut(String path, RequestBody body) {
        Request request = new Request.Builder()
                .url(path)
                .put(body).build();
        return client.newCall(request).execute();
    }

}