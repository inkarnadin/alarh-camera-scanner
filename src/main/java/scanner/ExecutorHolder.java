package scanner;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static scanner.Preferences.THREADS;

/**
 * Компонент управления потоками.
 *
 * @author inkarnadin
 */
public class ExecutorHolder {

    public static final int COUNT_THREADS = Integer.parseInt(Preferences.get(THREADS));
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(COUNT_THREADS);

    /**
     * Метод задержки выполнения потока.
     *
     * @param timeout тайм-аут
     */
    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void await(Long timeout) {
        EXECUTOR_SERVICE.awaitTermination(timeout, TimeUnit.MILLISECONDS);
    }

}