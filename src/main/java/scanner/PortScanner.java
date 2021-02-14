package scanner;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PortScanner {

    private final List<InetSocketAddress> addresses = new ArrayList<>();
    private final Converter converter = new Converter();

    private final List<String> result = new ArrayList<>();

    public void prepareSinglePortScanning(String rangeAsString, int port) {
        addresses.clear();
        IpV4Range rangeContainer = new IpV4Range(rangeAsString);
        List<IpV4Address> range = rangeContainer.range();
        for (IpV4Address address : range)
            addresses.add(converter.convert(address, port));
    }

    @SneakyThrows
    public void scanning() {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        HashSet<PortExecutor> callables = new HashSet<>();

        for (InetSocketAddress address : addresses)
            callables.add(new PortExecutor(address));

        List<Future<Optional<String>>> futures = executorService.invokeAll(callables);
        for (Future<Optional<String>> future : futures)
            future.get().ifPresent(System.out::println);
    }

}
