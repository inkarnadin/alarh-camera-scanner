package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.ExecutorHolder;
import scanner.Preferences;
import scanner.cve.CVEScanner;
import scanner.ffmpeg.FFmpegExecutor;
import scanner.rtsp.RTSPContext;
import scanner.rtsp.TransportMode;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static scanner.Preferences.ALLOW_FRAME_SAVING;
import static scanner.Preferences.ALLOW_UNTRUSTED_HOST;

/**
 * Brute force attack basic class.
 *
 * @author inkarnadin
 */
@Slf4j
public class BruteForceScanner {

    private final static long terminationTimeout = 1000L;

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
            writeLog(ip, Collections.singletonList(cveResult.get()), "<cve empty name>");
            if (Preferences.check(ALLOW_FRAME_SAVING))
                new FFmpegExecutor().saveFrame(cveResult.get(), ip);
            return;
        }

        if (isEmptyBruteTask(ip))
            return;

        int threads = ExecutorHolder.getCountThreads();
        int threshold = (threads - (passwords.length % threads) + passwords.length) / threads;
        List<CompletableFuture<AuthContainer>> futures = new ArrayList<>();
        for (int j = 0; j < passwords.length; j += threshold)
            futures.add(createBruteTask(ip, Arrays.copyOfRange(passwords, j, Math.min(j + threshold, passwords.length))));

        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(i -> i.getIp().equals(ip))
                .flatMap(x -> x.getOnlyAuth().stream())
                .collect(Collectors.toList());

        writeLog(ip, results, null);

        ExecutorHolder.await(terminationTimeout);
    }

    private CompletableFuture<AuthContainer> createBruteTask(String ip, String[] passwords) {
        CompletableFuture<AuthContainer> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> new BruteTask(future, ip, passwords).run(), ExecutorHolder.getExecutorService());
        return future;
    }

    private boolean isEmptyBruteTask(String ip) {
        RTSPContext.set(ip, TransportMode.ORTHODOX);
        CompletableFuture<AuthContainer> bruteTask = createBruteTask(ip, new String[] { null });
        AuthContainer result = bruteTask.join();

        // if true - skip further brute with credentials
        switch (result.getEmptyCredentialsAuth()) {
            case AUTH:
                writeLog(ip, new ArrayList<>(), null);
                return true;
            case NOT_AVAILABLE:
                return !Preferences.check(ALLOW_UNTRUSTED_HOST);
            case UNKNOWN_STATE:
                return true;
            default:
                return false;
        }
    }

    private void writeLog(String ip, List<String> results, String name) {
        String credentials = results.size() == 1 ? results.get(0) : ":";
        String path = "11";
        String localName = (Objects.isNull(name)) ? "<empty name>" : name;

        log.info("{}:{}:{}:{}", ip, path, credentials, localName);
    }

}