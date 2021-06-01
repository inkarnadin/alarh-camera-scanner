package scanner.ffmpeg;

import scanner.analyze.AuthResolve;
import scanner.analyze.PathResolve;
import scanner.analyze.ProblemHolder;
import scanner.analyze.ProblemTarget;
import scanner.stat.ScreenStatItem;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static scanner.stat.ScreenStatItem.*;
import static scanner.stat.StatDataHolder.SCREEN_GATHERER;

/**
 * Analyze FFmpeg log output.
 *
 * @author inkarnadin
 */
public class FFmpegInspector {

    /**
     * Analyzes the source and calculates statistics and determines further actions.
     *
     * @param source ffmpeg output as string
     * @param ip target address
     * @param credentials target login and password
     */
    public static void inspect(String source, String ip, String credentials) {
        checkError(source).ifPresent(x -> process(x, ip, credentials));
    }

    private static void process(ScreenStatItem item, String ip, String credentials) {
        boolean result;
        switch (item) {
            case STREAM_NOT_FOUND:
                result = ProblemHolder.save(new ProblemTarget<>(ip, credentials, new PathResolve()));
                break;
            case WRONG_AUTH_ERROR:
                result = ProblemHolder.save(new ProblemTarget<>(ip, credentials, new AuthResolve()));
                break;
            default:
                result = false;
        }

        if (!result)
            SCREEN_GATHERER.increment(item);
    }

    private static Optional<ScreenStatItem> checkError(String source) {
        Matcher mather;

        mather = Pattern.compile("Could not find codec").matcher(source);
        if (mather.find())
            return Optional.of(NOT_FOUND_CODEC_ERROR);

        mather = Pattern.compile("Invalid data found").matcher(source);
        if (mather.find())
            return Optional.of(INVALID_DATA_FOUND);

        mather = Pattern.compile("401 Unauthorized").matcher(source);
        if (mather.find())
            return Optional.of(WRONG_AUTH_ERROR);

        mather = Pattern.compile("461 Unkown").matcher(source);
        if (mather.find())
            return Optional.of(PROTOCOL_NOT_SUPPORTED);

        mather = Pattern.compile("451 Parameter Not Understood").matcher(source);
        if (mather.find())
            return Optional.of(PARAMETER_NOT_UNDERSTOOD);

        mather = Pattern.compile("404 Stream Not Found").matcher(source);
        if (mather.find())
            return Optional.of(STREAM_NOT_FOUND);

        mather = Pattern.compile("400 Bad Request").matcher(source);
        if (mather.find())
            return Optional.of(BAD_REQUEST);

        return Optional.empty();
    }

}