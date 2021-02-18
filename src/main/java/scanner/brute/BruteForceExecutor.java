package scanner.brute;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class BruteForceExecutor implements Callable<Void> {

    private final static String defaultLogin = "admin";

    private final String ip;
    private final String pass;
    private final AuthStateStore auth;

    @Override
    @SneakyThrows
    public Void call() {
        AuthState state = RTSPConnector.describe(ip, defaultLogin, pass);
        auth.setState(state);
        auth.setCredentials((auth.isAuth())
                    ? Optional.of(defaultLogin + ":" + pass)
                    : Optional.empty());
        return null;
    }

}