package scanner.analyze;

import scanner.ffmpeg.FFmpegPath;

/**
 * Stream path problem solution.
 *
 * @author inkarnadin
 */
public class PathResolve implements Resolve<FFmpegPath> {

    /**
     * Get solution.
     *
     * @return base stream path for trying checking
     */
    @Override
    public FFmpegPath resolve() {
        return FFmpegPath.BASE;
    }

}