package scanner;

import lombok.Getter;
import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static scanner.Preferences.THREADS;

/**
 * Threads manager class.
 *
 * @author inkarnadin
 */
public class ExecutorHolder {

    @Getter
    private static final int countThreads = Integer.parseInt(Preferences.get(THREADS));

    @Getter
    private static final ExecutorService executorService = Executors.newFixedThreadPool(countThreads);

    @SneakyThrows
    public static void await(Long timeout) {
        executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
    }


}