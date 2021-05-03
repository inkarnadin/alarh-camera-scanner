package scanner.scan;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Port scanning basic class.
 *
 * @author inkarnadin
 */
@Slf4j
public class CameraScanner {

    private final Queue<InetSocketAddress> addresses = new ArrayDeque<>();

    private final static int countThreads = Integer.parseInt(Preferences.get("-t"));

    private final ExecutorService executorService = Executors.newFixedThreadPool(countThreads);

    private final static long TERMINATION_TIMEOUT = 500L;

    /**
     * Start scanning certain address by prepared IPs ranges and settings port.
     */
    @SneakyThrows
    public void scanning(List<InetSocketAddress> list) {
        addresses.addAll(list);

        List<CompletableFuture<Optional<String>>> futures = new ArrayList<>();
        while (!addresses.isEmpty())
            futures.add(createCameraScanTask(addresses.poll()));

        List<Optional<String>> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        results.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(log::info);
        executorService.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private CompletableFuture<Optional<String>> createCameraScanTask(InetSocketAddress address) {
        CompletableFuture<Optional<String>> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> new CameraScanTask(future, address).run(), executorService);
        return future;
    }

}
