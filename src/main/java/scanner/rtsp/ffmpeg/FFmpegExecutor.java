package scanner.rtsp.ffmpeg;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import static scanner.rtsp.ffmpeg.FFmpegState.BASIC;

/**
 * FFmpeg manipulation control class.
 *
 * @author inkarnadin
 */
@Slf4j
public class FFmpegExecutor {

    public static void saveFrame(String credentials, String ip) {
        try {
            CompletableFuture<FFmpegState> future = CompletableFuture.supplyAsync(new FFmpegFrameReader(ip, credentials, BASIC));
            FFmpegState state = future.get();
            if (state != FFmpegState.COMPLETE)
                CompletableFuture.supplyAsync(new FFmpegFrameReader(ip, credentials, state)).get();
        } catch (Exception xep) {
            log.warn("FFmpeg executor error: {}", xep.getMessage());
        }
    }

}