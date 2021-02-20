package scanner.brute;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class BruteForceExecutor implements Callable<AuthStateStore> {

    private final static String defaultLogin = "admin";

    private final String ip;
    private final String pass;

    @Override
    @SneakyThrows
    synchronized public AuthStateStore call() {
        String credentials = Objects.nonNull(pass)
                ? String.format("%s:%s", defaultLogin, pass)
                : "";
        AuthState state = RTSPConnector.describe(ip, credentials);
        AuthStateStore auth = new AuthStateStore();
        auth.setState(state);
        auth.setCredentials((auth.isAuth())
                    ? Optional.of(credentials)
                    : Optional.empty());
        return auth;
    }

}