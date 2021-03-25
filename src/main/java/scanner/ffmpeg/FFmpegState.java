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

    SIMPLE("/11"),
    COMPLETE("");

    @Getter
    private final String path;

}