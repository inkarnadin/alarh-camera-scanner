package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static scanner.brute.AuthState.*;

@Slf4j
@Deprecated
public class BruteForceScanner {

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        AuthStateStore auth = new AuthStateStore();

        if (IpBruteFilter.excludeFakeCamera(ip))
            return;

        checkEmptyCredentials(auth, ip);

        int successAttemptCounter = 0;
        HashSet<BruteForceExecutor> requests = new HashSet<>();
        for (int i = 0; i < passwords.length; i++) {
            if (Arrays.asList(NOT_REQUIRED, NOT_AVAILABLE).contains(auth.getState()))
                break;

            if (auth.getState() == AUTH && successAttemptCounter > 1) {
                auth.setState(NOT_REQUIRED);
                auth.setCredentials(Optional.empty());
            }

            requests.add(new BruteForceExecutor(ip, passwords[i]));
            if (requests.size() == 5 || i == passwords.length - 1) {
                List<Future<AuthStateStore>> futures = executorService.invokeAll(requests, 2L, TimeUnit.SECONDS);
                for (Future<AuthStateStore> future : futures) {
                    try {
                        AuthStateStore authNew = future.get();
                        if (authNew.isAuth()) {
                            if (!auth.isAuth()) {
                                auth.setCredentials(authNew.getCredentials());
                                auth.setState(authNew.getState());
                            }
                            successAttemptCounter++;
                        }
                    } catch (CancellationException | ExecutionException | InterruptedException ce) {
                        auth.setState(NOT_AUTH);
                        future.cancel(true);
                    }
                }
                Thread.sleep(200);
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

    @SneakyThrows
    private void checkEmptyCredentials(AuthStateStore auth, String ip) {
        Future<AuthStateStore> emptyCredentialsFuture = executorService.submit(new BruteForceExecutor(ip, null));
        try {
            AuthState emptyCredentialsState = emptyCredentialsFuture.get(3L, TimeUnit.SECONDS).getState();
            if (emptyCredentialsState == AuthState.AUTH)
                auth.setState(NOT_REQUIRED);
        } catch (TimeoutException | CancellationException | ExecutionException xep) {
            auth.setState(NOT_AVAILABLE);
            emptyCredentialsFuture.cancel(true);
        }
    }

}