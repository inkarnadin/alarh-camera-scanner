package scanner.ffmpeg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Basic RTSP stream path.
 *
 * @author inkarnadin
 */
@RequiredArgsConstructor
public enum FFmpegState {

    STANDARD("/11");

    @Getter
    private final String path;

}