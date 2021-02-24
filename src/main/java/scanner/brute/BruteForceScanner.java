package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Context;
import scanner.Preferences;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class BruteForceScanner {

    private final ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Preferences.get("-t")));

    private final static long EXEC_TIMEOUT = 5000L;
    private final static long TERMINATION_TIMEOUT = 500L;

    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        switch (execEmptyBruteTask(ip)) {
            case AUTH:
                log.info("{} => {}", ip, "auth not required");
                return;
            case NOT_AVAILABLE:
                if (!Preferences.check("-uc"))
                    return;
            default:
                break;
        }

        List<CompletableFuture<AuthStateStore>> futures = Arrays.stream(passwords)
                .map(f -> createBruteTask(ip, f))
                .collect(Collectors.toList());

        List<AuthStateStore> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        List<AuthStateStore> authList = results.stream()
                .filter(AuthStateStore::isAuth)
                .collect(Collectors.toList());
        int size = authList.size();

        if (size > 0) {
            log.info("{} => {}", ip, (size == 1)
                    ? authList.get(0).getCredentials().orElse("auth not required")
                    : "auth not required");

            executorService.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    private CompletableFuture<AuthStateStore> createBruteTask(String ip, String password) {
        CompletableFuture<AuthStateStore> future = new CompletableFuture<AuthStateStore>()
                .completeOnTimeout(AuthStateStore.BAD_AUTH, EXEC_TIMEOUT, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(() -> new BruteTask(future, ip, password).run(), executorService);
        return future;
    }

    private AuthState execEmptyBruteTask(String ip) {
        CompletableFuture<AuthStateStore> future = new CompletableFuture<AuthStateStore>()
                .completeOnTimeout(AuthStateStore.BAD_AUTH, EXEC_TIMEOUT, TimeUnit.MILLISECONDS);

        Context.set(ip, RTSPMode.ORTHODOX);
        CompletableFuture.runAsync(() -> new BruteTask(future, ip, null).run(), executorService);

        AuthStateStore result = future.join();
        return result.getState();
    }

}