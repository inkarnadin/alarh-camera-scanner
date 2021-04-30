package scanner.analyze;

import scanner.ffmpeg.FFmpegPath;

public class PathResolve implements Resolve<FFmpegPath> {

    @Override
    public FFmpegPath resolve() {
        return FFmpegPath.BASE;
    }

}