package scanner;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.cve.CVEScanner;
import scanner.http.IpV4Address;
import scanner.http.IpV4Range;

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

    private final static int port = Integer.parseInt(Preferences.get("-p"));
    private final static int countThreads = Integer.parseInt(Preferences.get("-t"));

    private final ExecutorService executorService = Executors.newFixedThreadPool(countThreads);

    private final static long TERMINATION_TIMEOUT = 500L;

    /**
     * Prepare scanning ranges.
     *
     * @param rangeAsString range of IPs in string view, ex. 10.20.3.0-10.20.4.255
     * @return count IP addresses by range.
     */
    public int prepareSinglePortScanning(String rangeAsString) {
        IpV4Range rangeContainer = new IpV4Range(rangeAsString);
        List<IpV4Address> range = rangeContainer.range();
        for (IpV4Address address : range)
            addresses.add(new InetSocketAddress(address.getIpAsString(), port));
        return addresses.size();
    }

    /**
     * Start scanning certain address by prepared IPs ranges and settings port.
     */
    @SneakyThrows
    public void scanning() {
        List<CompletableFuture<Optional<String>>> futures = new ArrayList<>();
        while (!addresses.isEmpty())
            futures.add(createCameraScanTask(addresses.poll()));

        List<Optional<String>> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        results.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(CVEScanner::scanning);
        executorService.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private CompletableFuture<Optional<String>> createCameraScanTask(InetSocketAddress address) {
        CompletableFuture<Optional<String>> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> new CameraScanTask(future, address).run(), executorService);
        return future;
    }

}
