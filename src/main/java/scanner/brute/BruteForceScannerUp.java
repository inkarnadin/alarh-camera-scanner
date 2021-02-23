package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class BruteForceScannerUp {

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        if (checkEmptyCredentials()) {
            log.info("{} => {}", ip, "auth not required");
            return;
        }

        List<CompletableFuture<AuthStateStore>> futures = Arrays.stream(passwords)
                .map(f -> getBrute(ip, f))
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

            try {
                executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {}
        }
    }

    private CompletableFuture<AuthStateStore> getBrute(String ip, String password) {
        CompletableFuture<AuthStateStore> result = new CompletableFuture<AuthStateStore>()
                .completeOnTimeout(AuthStateStore.BAD_AUTH, 2500L, TimeUnit.MILLISECONDS);

        CompletableFuture.runAsync(() -> {
            try {
                BruteForceExecutor executor = new BruteForceExecutor(ip, password);
                AuthStateStore auth = executor.call();
                result.complete(auth);
            } catch (Exception e) {
                AuthStateStore auth = new AuthStateStore();
                result.complete(auth);
            }
        }, executorService);
        return result;
    }

    private boolean checkEmptyCredentials() {
        AuthStateStore result = new CompletableFuture<AuthStateStore>()
                .completeOnTimeout(AuthStateStore.BAD_AUTH, 5000L, TimeUnit.MILLISECONDS)
                .join();
        return (result.getState() == AuthState.AUTH);
    }

}