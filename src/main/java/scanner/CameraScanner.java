package scanner;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.cve.CVEScanner;
import scanner.http.Converter;
import scanner.http.IpV4Address;
import scanner.http.IpV4Range;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class CameraScanner {

    private final Queue<InetSocketAddress> addresses = new ArrayDeque<>();
    private final Converter converter = new Converter();

    ExecutorService executorService = Executors.newFixedThreadPool(20);

    public int prepareSinglePortScanning(String rangeAsString, int port) {
        IpV4Range rangeContainer = new IpV4Range(rangeAsString);
        List<IpV4Address> range = rangeContainer.range();
        for (IpV4Address address : range)
            addresses.add(converter.convert(address, port));
        return addresses.size();
    }

    @SneakyThrows
    public void scanning() {
        HashSet<CameraScanExecutor> callables = new HashSet<>();
        while (!addresses.isEmpty()) {
            callables.add(new CameraScanExecutor(addresses.poll()));
            if (callables.size() == 500 || addresses.isEmpty()) {
                List<Future<Optional<String>>> futures = executorService.invokeAll(callables);
                for (Future<Optional<String>> future : futures) {
                    Optional<String> result = future.get();
                    if (result.isPresent()) {
                        String value = result.get();
                        log.info(value);

                        CVEScanner.scanning(value);
                    }
                }
                callables.clear();
            }
        }
    }
}
