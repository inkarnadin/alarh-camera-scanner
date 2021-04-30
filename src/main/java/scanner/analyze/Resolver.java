package scanner.analyze;

import scanner.ffmpeg.FFmpegExecutor;
import scanner.ffmpeg.FFmpegPath;

/**
 * Rerun problem target according to the specified amendments.
 *
 * @author inkarnadin
 */
public class Resolver {

    /**
     * Rerun problem target which was saved in {@code ProblemHolder} class.
     */
    @SuppressWarnings({"rawtypes"})
    public static void run() {
        for (ProblemTarget target : ProblemHolder.getStore()) {
            Resolve value = target.getResolve();
            if (value instanceof PathResolve) {
                new FFmpegExecutor().saveFrame(target.getCredentials(), target.getIp(), ((PathResolve) value).resolve());
            }
            if (value instanceof AuthResolve) {
                new FFmpegExecutor().saveFrame(((AuthResolve) value).resolve(), target.getIp(), FFmpegPath.STANDARD);
            }
        }
    }

}