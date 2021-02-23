package scanner;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.cve.CVEScanner;
import scanner.http.Converter;
import scanner.http.IpV4Address;
import scanner.http.IpV4Range;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class CameraScanner {

    private final Queue<InetSocketAddress> addresses = new ArrayDeque<>();
    private final Converter converter = new Converter();

    ExecutorService executorService = Executors.newFixedThreadPool(100);

    private final static long TERMINATION_TIMEOUT = 500L;

    public int prepareSinglePortScanning(String rangeAsString, int port) {
        IpV4Range rangeContainer = new IpV4Range(rangeAsString);
        List<IpV4Address> range = rangeContainer.range();
        for (IpV4Address address : range)
            addresses.add(converter.convert(address, port));
        return addresses.size();
    }

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
                .peek(log::info)
                .forEach(CVEScanner::scanning);
        executorService.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private CompletableFuture<Optional<String>> createCameraScanTask(InetSocketAddress address) {
        CompletableFuture<Optional<String>> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> new CameraScanTask(future, address).run(), executorService);
        return future;
    }

}
