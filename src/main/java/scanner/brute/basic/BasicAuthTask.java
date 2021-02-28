package scanner.brute.basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import scanner.brute.AuthContainer;
import scanner.brute.AuthState;
import scanner.http.HttpClient;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Basic Auth attack subtask class.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class BasicAuthTask implements Runnable {

    private final static String defaultLogin = "admin";

    private final CompletableFuture<AuthContainer> future;
    private final String ip;
    private final String password;

    /**
     * Run simple get request for check Basic Auth certain ip address and password.
     */
    @Override
    public void run() {
        int num = ThreadLocalRandom.current().nextInt(0, 10);

        Thread.currentThread().setName(String.format("brute-%s-%s...-%s", ip, password, num));
        AuthContainer auth = new AuthContainer(ip);

        String credentials = String.format("%s:%s", defaultLogin, password);
        try (Response response = HttpClient.executeBasicAuth(ip, new String(Base64.getEncoder().encode(credentials.getBytes())))) {
            auth.put(credentials, response.code() == 200 ? AuthState.AUTH : AuthState.NOT_AUTH);
            log.debug("response code: {}", response.code());
            future.complete(auth);
        } catch (Exception xep) {
            log.warn("request failed: {}", xep.getMessage());
            future.complete(auth);
        }
    }

}