package scanner.ffmpeg;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

import static scanner.ffmpeg.FFmpegState.STANDARD;

/**
 * FFmpeg manipulation control class.
 *
 * @author inkarnadin
 */
@Slf4j
public class FFmpegExecutor {

    /**
     * Save one frame via FFmpeg and kill external process.
     *
     * @param credentials login and password or null if absent
     * @param ip target address
     */
    @SneakyThrows
    public void saveFrame(String credentials, String ip) {
       CompletableFuture.supplyAsync(() -> new FFmpegFrameSaver(ip, credentials, STANDARD).get())
                        .thenAccept(x -> {
                            if (x.isAlive())
                                x.destroy();
                            log.info("Process ({}) was ended successfully", x.pid());
                        })
               .get();
    }

}