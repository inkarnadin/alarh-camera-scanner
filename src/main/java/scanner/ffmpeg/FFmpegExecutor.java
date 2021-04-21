package scanner.ffmpeg;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static scanner.ffmpeg.FFmpegState.SIMPLE;

/**
 * FFmpeg manipulation control class.
 *
 * @author inkarnadin
 */
@Slf4j
public class FFmpegExecutor {

    /**
     * Save one frame.
     *
     * @param credentials login and password or null if absent.
     * @param ip target address.
     */
    public static void saveFrame(String credentials, String ip) {
        try {
            CompletableFuture<FFmpegState> future = CompletableFuture.supplyAsync(new FFmpegFrameReader(ip, credentials, SIMPLE))
                    .orTimeout(5, TimeUnit.SECONDS);
            FFmpegState state = future.get();
            if (state != FFmpegState.COMPLETE)
                CompletableFuture.supplyAsync(new FFmpegFrameReader(ip, credentials, state))
                        .orTimeout(5, TimeUnit.SECONDS)
                        .get();
        } catch (Exception xep) {
            log.warn("FFmpeg executor error: {} {}", ip, xep.getMessage());
        }
    }

}