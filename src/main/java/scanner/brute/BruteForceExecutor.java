package scanner.brute;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class BruteForceExecutor implements Callable<Optional<String>> {

    private final String ip;
    private final String pass;

    @Override
    @SneakyThrows
    public Optional<String> call() {
        boolean isAuth = RTSPConnector.describe(ip, "admin", pass);
        return (isAuth)
                ? Optional.of(ip + " => admin:" + pass)
                : Optional.empty();
    }

}