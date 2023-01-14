package scanner.ffmpeg;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.rtsp.RTSPPath;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Класс сохранения изображения полученного одним кадром из видео-потока (через внешнее приложение FFMpeg).
 * <p>При создании требуется указать целевой адрес, учетные данные (необязательно) и корректный базовый путь потока.
 *
 * @author inkarnadin
 * on 01-10-2022
 */
@Slf4j
@RequiredArgsConstructor
public class FFMpegFrameSaver implements Supplier<Process> {

    private final static int defaultTimeout = 15;

    private final String ip;
    private final String credentials;
    private final RTSPPath path;

    /**
     * Метод сохранения изображения в качестве файла JPG.
     * <p>Сохранение производится с помощью внешнего приложения FFMpeg, после попытки сохранения внешний системный процесс
     * завершается.
     *
     * @return объект выполнения внешнего процесса
     */
    @SneakyThrows
    public Process get() {
        ProcessBuilder builder = createProcess();
        Process process = builder.start();

        Thread.currentThread().setName(String.format("ffmpeg-%s-%s...-%s", ip, credentials, process.pid()));

        CompletableFuture.runAsync(() -> new FFMpegLogReader(process).run())
                .orTimeout(defaultTimeout, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    new FFMpegProcessKiller(process).kill();
                    return null;
                }).get();

        return process;
    }

    /**
     * Метод создания внешнего процесса согласно заданным параметрам.
     *
     * @return объект создания внешней команды
     */
    private ProcessBuilder createProcess() {
        String url = (Objects.nonNull(credentials))
                ? String.format("rtsp://%s@%s%s", credentials, ip, path.getValue())
                : String.format("rtsp://%s%s", ip, path.getValue());

        return new ProcessBuilder()
                .redirectErrorStream(true)
                .command(
                        "ffmpeg",
                        "-y",
                        "-hide_banner",
                        "-rtsp_transport", "tcp",
                        "-i", url,
                        "-vframes", "1",
                        "-q:v", "2", String.format("result/screen/%s.jpg", ip));
    }

}