package scanner.ffmpeg;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.stat.ScreenStatItem;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static scanner.ffmpeg.FFmpegPath.STANDARD;
import static scanner.stat.StatDataHolder.SCREEN_GATHERER;

/**
 * FFmpeg manipulation control class.
 *
 * @author inkarnadin
 */
@Slf4j
public class FFmpegExecutor {

    /**
     * Save one frame via FFmpeg and kill external process.
     * After execution checked that frame download successfully and was saved.
     *
     * @param credentials login and password or null if absent
     * @param ip target address
     * @param path rtsp stream path
     */
    @SneakyThrows
    public void saveFrame(String credentials, String ip, FFmpegPath path) {
        CompletableFuture.supplyAsync(() -> new FFmpegFrameSaver(ip, credentials, path).get())
                .thenAccept(x -> {
                    if (x.isAlive())
                        x.destroy();
                    log.info("Process ({}) was ended successfully", x.pid());
                })
                .get();

        File file = new File(String.format("result/screen/%s.jpg", ip));
        if (file.exists())
            SCREEN_GATHERER.increment(ScreenStatItem.SUCCESS);
    }

    /**
     * Save one frame via FFmpeg and kill external process.
     * After execution checked that frame download successfully and was saved.
     * Use default stream path.
     *
     * @param credentials login and password or null if absent
     * @param ip target address
     */
    @SneakyThrows
    public void saveFrame(String credentials, String ip) {
        SCREEN_GATHERER.increment(ScreenStatItem.ALL);
        this.saveFrame(credentials, ip, STANDARD);
    }

}