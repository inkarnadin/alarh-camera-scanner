package scanner.rtsp.ffmpeg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FFmpegState {

    BASIC("/Streaming/Channels/101"),
    PATH_11("/11"),
    COMPLETE("");

    @Getter
    private final String path;

}