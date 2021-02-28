package scanner.brute.basic;

import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.brute.AuthContainer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scanner class for checking Basic Authentication
 *
 * @author inkarnadin
 */
@Slf4j
public class BasicAuthScanner {

    private final static int countThreads = Integer.parseInt(Preferences.get("-t"));
    private final ExecutorService executorService = Executors.newFixedThreadPool(countThreads);

    public void brute(String ip, String[] passwords) {
        List<CompletableFuture<AuthContainer>> futures = Stream.of(passwords)
                .map(x -> createBasicAuthBruteTask(ip, x))
                .collect(Collectors.toList());

        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(i -> i.getIp().equals(ip))
                .flatMap(x -> x.getOnlyAuth().stream())
                .collect(Collectors.toList());

        if (!results.isEmpty())
            log.info("{} => {} BASIC AUTH", ip, results.get(0));
    }

    private CompletableFuture<AuthContainer> createBasicAuthBruteTask(String ip, String password) {
        CompletableFuture<AuthContainer> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> new BasicAuthTask(future, ip, password).run(), executorService);
        return future;
    }

}