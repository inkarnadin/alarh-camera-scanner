package scanner;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.http.HttpClient;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class BruteForceScanner {

    private final String BRUTE = "http://%s/system/deviceInfo";
    private final HttpClient client = new HttpClient();

    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        HashSet<BruteForceExecutor> requests = new HashSet<>();
        for (int i = 0; i < passwords.length; i++) {
            if (requests.size() == 20 || i == passwords.length - 1) {
                List<Future<Optional<String>>> futures = executorService.invokeAll(requests);
                for (Future<Optional<String>> future : futures)
                    future.get().ifPresent(log::info);
                requests.clear();
            }
            requests.add(new BruteForceExecutor(client, ip, passwords[i]));
        }
    }

}