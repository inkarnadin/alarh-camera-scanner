package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Context;
import scanner.Preferences;
import scanner.cve.CVEScanner;
import scanner.ffmpeg.FFmpegExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Brute force attack basic class.
 *
 * @author inkarnadin
 */
@Slf4j
public class BruteForceScanner {

    private final static long termination_timeout = 1000L;

    private final static int countThreads = Integer.parseInt(Preferences.get("-t"));
    private final ExecutorService executorService = Executors.newFixedThreadPool(countThreads);

    /**
     * Start brute certain address by prepared range passwords list.
     *
     * @param ip target IP address.
     * @param passwords passwords array.
     */
    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        Optional<String> cveResult = CVEScanner.scanning(ip);
        if (cveResult.isPresent()) {
            log.info("{} => {}", ip, cveResult.get());
            FFmpegExecutor.saveFrame(cveResult.get(), ip);
            return;
        }

        if (isEmptyBruteTask(ip))
            return;

        int threshold = (countThreads - (passwords.length % countThreads) + passwords.length) / countThreads;
        List<CompletableFuture<AuthContainer>> futures = new ArrayList<>();
        for (int j = 0; j < passwords.length; j += threshold)
            futures.add(createBruteTask(ip, Arrays.copyOfRange(passwords, j, Math.min(j + threshold, passwords.length))));

        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(i -> i.getIp().equals(ip))
                .flatMap(x -> x.getOnlyAuth().stream())
                .collect(Collectors.toList());
        int size = results.size();

        if (size > 0)
            log.info("{} => {}", ip, (size == 1)
                    ? results.get(0)
                    : "auth not required");

        executorService.awaitTermination(termination_timeout, TimeUnit.MILLISECONDS);
    }

    private CompletableFuture<AuthContainer> createBruteTask(String ip, String[] passwords) {
        CompletableFuture<AuthContainer> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> new BruteTask(future, ip, passwords).run(), executorService);
        return future;
    }

    private boolean isEmptyBruteTask(String ip) {
        Context.set(ip, TransportMode.ORTHODOX);
        CompletableFuture<AuthContainer> bruteTask = createBruteTask(ip, new String[] { null });
        AuthContainer result = bruteTask.join();

        switch (result.getEmptyCredentialsAuth()) {
            case AUTH:
                log.info("{} => {}", ip, "auth not required");
                return true;
            case NOT_AVAILABLE:
                return !Preferences.check("-uc");
            case UNKNOWN_STATE:
                return true;
            default:
                return false;
        }
    }

}