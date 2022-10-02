package scanner.ffmpeg;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Frame reader for saving screenshot (via FFmpeg).
 * Allow ip, credentials and state of stream - or right path, or signal about finished.
 *
 * Try get one frame and save it as image.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class FFmpegFrameSaver implements Supplier<Process> {

    private final static int defaultTimeout = 15;

    private final String ip;
    private final String credentials;
    private final FFmpegPath state;

    /**
     * Save frame as JPG image. Kill ffmpeg process if run out of time.
     *
     * @return state of subtask
     */
    @SneakyThrows
    public Process get() {
        ProcessBuilder builder = createProcess();
        Process process = builder.start();

        Thread.currentThread().setName(String.format("ffmpeg-%s-%s...-%s", ip, credentials, process.pid()));
        //log.info("==========================");

        CompletableFuture.runAsync(() -> new FFmpegLogReader(process, ip, credentials).run())
                .orTimeout(defaultTimeout, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    new FFmpegProcessKiller(process).kill();
                    return null;
                }).get();

        return process;
    }

    private ProcessBuilder createProcess() {
        String url = (Objects.nonNull(credentials))
                ? String.format("rtsp://%s@%s%s", credentials, ip, state.getPath())
                : String.format("rtsp://%s%s", ip, state.getPath());

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