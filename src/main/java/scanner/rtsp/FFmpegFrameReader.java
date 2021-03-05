package scanner.rtsp;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Frame reader for saving screenshot (via FFmpeg).
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class FFmpegFrameReader implements Runnable {

    private final String credentials;
    private final String ip;

    /**
     * Run saving frame as JPG image.
     */
    @SneakyThrows
    @Override
    public void run() {
        try {
            String url = (credentials.length() > 0)
                    ? String.format("rtsp://%s@%s/Streaming/Channels/101", credentials, ip)
                    : String.format("rtsp://%s/Streaming/Channels/101", ip);

            new ProcessBuilder(
                    "ffmpeg",
                    "-rtsp_transport", "tcp",
                    "-i", url,
                    "-vframes", "1",
                    "-q:v", "2",
                    String.format("result/screen/%s.jpg", ip)).start();
        } catch (Exception e) {
            log.warn("FFmpeg error! {}", e.getMessage());
        }
    }

}