package scanner.runner.breaking.brute;

import lombok.RequiredArgsConstructor;
import scanner.runner.breaking.AuthContainer;
import scanner.rtsp.RTSPCredentialVerifier;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Brute force attack subtask class.
 *
 * @author inkarnadin
 */
@RequiredArgsConstructor
public class BruteTask implements Runnable {

    private final static String DEFAULT_LOGIN = "admin";

    private final CompletableFuture<AuthContainer> future;
    private final String ip;
    private final String[] passwords;

    /**
     * Opens a socket connection and begins iterating over the received credentials.
     */
    @Override
    public void run() {
        int num = ThreadLocalRandom.current().nextInt(100, 999);

        Thread.currentThread().setName(String.format("brute-%s-%s", ip, num));
        AuthContainer auth = new AuthContainer(ip);

        try (RTSPCredentialVerifier sender = new RTSPCredentialVerifier()) {
            sender.connect(ip);
            for (String password : passwords) {
                String credentials = Objects.nonNull(password)
                        ? String.format("%s:%s", DEFAULT_LOGIN, password)
                        : null;
                auth.put(credentials, sender.check(credentials));
            }
            future.complete(auth);
        } catch (Exception xep) {
            future.complete(auth);
        }
    }

}