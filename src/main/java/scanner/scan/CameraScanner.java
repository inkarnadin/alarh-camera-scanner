package scanner.scan;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.ExecutorHolder;
import scanner.stat.ScanStatItem;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static scanner.stat.StatDataHolder.SCAN_GATHERER;

/**
 * Port scanning basic class.
 *
 * @author inkarnadin
 */
@Slf4j
public class CameraScanner {

    private static final long TERMINATION_TIMEOUT = 500L;

    private final Queue<InetSocketAddress> addresses = new ArrayDeque<>();

    /**
     * Start scanning certain address by prepared IPs ranges and settings port.
     *
     * @return list of checked ip addresses
     */
    @SneakyThrows
    public Set<String> scanning(Set<InetSocketAddress> list) {
        addresses.addAll(list);

        List<CompletableFuture<Optional<String>>> futures = new ArrayList<>();
        while (!addresses.isEmpty())
            futures.add(createCameraScanTask(addresses.poll()));

        List<Optional<String>> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        Set<String> targetList = results.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(x -> SCAN_GATHERER.increment(ScanStatItem.SUCCESS))
                .peek(log::info)
                .collect(Collectors.toSet());

        ExecutorHolder.await(TERMINATION_TIMEOUT);
        return targetList;
    }

    private CompletableFuture<Optional<String>> createCameraScanTask(InetSocketAddress address) {
        CompletableFuture<Optional<String>> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> new CameraScanTask(future, address).run(), ExecutorHolder.EXECUTOR_SERVICE);
        return future;
    }

}
