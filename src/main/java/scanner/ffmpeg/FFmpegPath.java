package scanner.ffmpeg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Basic RTSP stream path.
 *
 * @author inkarnadin
 */
@RequiredArgsConstructor
public enum FFmpegPath {

    STANDARD("/11"),
    BASE("");

    @Getter
    private final String path;

}