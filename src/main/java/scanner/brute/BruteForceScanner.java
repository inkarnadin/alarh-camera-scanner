package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class BruteForceScanner {

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @SneakyThrows
    public void brute(String ip, String[] passwords) {
        List<String> result = new ArrayList<>();
        HashSet<BruteForceExecutor> requests = new HashSet<>();
        for (int i = 0; i < passwords.length; i++) {
            requests.add(new BruteForceExecutor(ip, passwords[i]));
            if (requests.size() == 20 || i == passwords.length - 1) {
                List<Future<Optional<String>>> futures = executorService.invokeAll(requests, 2L, TimeUnit.SECONDS);
                for (Future<Optional<String>> future : futures) {
                    try {
                        future.get().ifPresent(result::add);
                    } catch (CancellationException | ExecutionException | InterruptedException ce) {
                        future.cancel(true);
                    }
                }
                requests.clear();
            }
        }
        switch (result.size()) {
            case 1: log.info("{} => {}", ip, result.get(0)); break;
            case 0: break;
            default: log.info("{} => Auth not required?", ip);
        }
    }

}