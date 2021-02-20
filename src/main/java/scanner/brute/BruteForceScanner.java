package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
public class BruteForceScanner {

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        AuthStateStore auth = new AuthStateStore();

        if (IpBruteFilter.excludeFakeCamera(ip))
            return;

        int successTryingCounter = 0;
        HashSet<BruteForceExecutor> requests = new HashSet<>();
        for (int i = 0; i < passwords.length; i++) {
            if (auth.isAuth() && successTryingCounter > 1) {
                auth.setCredentials(Optional.empty());
                auth.setState(AuthState.NOT_REQUIRED);
                break;
            }

            requests.add(new BruteForceExecutor(ip, passwords[i]));
            if (requests.size() == 5 || i == passwords.length - 1) {
                List<Future<AuthStateStore>> futures = executorService.invokeAll(requests, 4L, TimeUnit.SECONDS);
                for (Future<AuthStateStore> future : futures) {
                    try {
                        AuthStateStore authNew = future.get();
                        if (authNew.isAuth()) {
                            if (!auth.isAuth()) {
                                auth.setCredentials(authNew.getCredentials());
                                auth.setState(authNew.getState());
                            }
                            successTryingCounter++;
                        }
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
            case UNKNOWN_STATE: // maybe basic request must be changed manually
            case NOT_AVAILABLE: log.debug("{} => skipped, not available", ip); // maybe it will be deleted as "bad cameras"
            case NOT_AUTH:
            default: break;
        }
    }

}