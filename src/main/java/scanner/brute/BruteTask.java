package scanner.brute;

import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class BruteTask implements Runnable {

    private final static String defaultLogin = "admin";

    private final CompletableFuture<AuthStateStore> future;
    private final String ip;
    private final String password;

    public void run() {
        try {
            String credentials = Objects.nonNull(password) ? String.format("%s:%s", defaultLogin, password) : "";
            AuthState state = RTSPConnector.describe(ip, credentials);
            AuthStateStore auth = new AuthStateStore();
            auth.setState(state);
            auth.setCredentials((auth.isAuth()) ? Optional.of(credentials) : Optional.empty());
            future.complete(auth);
        } catch (Exception e) {
            AuthStateStore auth = new AuthStateStore();
            future.complete(auth);
        }
    }

}