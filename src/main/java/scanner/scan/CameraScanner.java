package scanner.scan;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.stat.ScanStatEnum;
import scanner.stat.ScanStatGatherer;

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

    private final static long termination_timeout = 500L;

    private final Queue<InetSocketAddress> addresses = new ArrayDeque<>();

    private final int countThreads = Integer.parseInt(Preferences.get("-t"));

    private final ExecutorService executorService = Executors.newFixedThreadPool(countThreads);

    /**
     * Start scanning certain address by prepared IPs ranges and settings port.
     *
     * @return list of checked ip addresses
     */
    @SneakyThrows
    public List<String> scanning(List<InetSocketAddress> list) {
        addresses.addAll(list);

        List<CompletableFuture<Optional<String>>> futures = new ArrayList<>();
        while (!addresses.isEmpty())
            futures.add(createCameraScanTask(addresses.poll()));

        List<Optional<String>> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        List<String> targetList = results.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(x -> ScanStatGatherer.increment(ScanStatEnum.SUCCESS))
                .peek(log::info)
                .collect(Collectors.toList());

        executorService.awaitTermination(termination_timeout, TimeUnit.MILLISECONDS);
        return targetList;
    }

    private CompletableFuture<Optional<String>> createCameraScanTask(InetSocketAddress address) {
        CompletableFuture<Optional<String>> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> new CameraScanTask(future, address).run(), executorService);
        return future;
    }

}
