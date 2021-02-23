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
        AuthStateStore auth = new AuthStateStore();
        try {
            String credentials = Objects.nonNull(password)
                    ? String.format("%s:%s", defaultLogin, password)
                    : "";
            AuthState state = RTSPConnector.describe(ip, credentials);
            auth.setState(state);
            auth.setCredentials(auth.isAuth()
                    ? Optional.of(credentials)
                    : Optional.empty());
            future.complete(auth);
        } catch (Exception e) {
            future.complete(auth);
        }
    }

}