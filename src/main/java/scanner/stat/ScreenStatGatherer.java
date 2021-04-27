package scanner.stat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static scanner.stat.ScreenStatEnum.*;

/**
 * Screenshot save statistic class.
 *
 * @author inkarnadin
 */
public class ScreenStatGatherer {

    private static final List<ScreenStatEnum> errorStateList = Arrays.asList(
            NOT_FOUND_CODEC_ERROR,
            INVALID_DATA_FOUND,
            WRONG_AUTH_ERROR,
            STREAM_NOT_FOUND,
            BAD_REQUEST,
            UNEXPECTED_ERROR,
            OTHER
    );

    private static final Map<ScreenStatEnum, Integer> screenStat = new HashMap<>() {{
        put(ALL, 0);
        put(SUCCESS, 0);
        put(FAILURE, 0);

        put(INVALID_DATA_FOUND, 0);
        put(WRONG_AUTH_ERROR, 0);
        put(NOT_FOUND_CODEC_ERROR, 0);
        put(STREAM_NOT_FOUND, 0);
        put(BAD_REQUEST, 0);
        put(UNEXPECTED_ERROR, 0);
        put(OTHER, 0);
    }};

    /**
     * Gather info about rtsp server and streaming status from ffmpeg output.
     *
     * @param source ffmpeg output string
     */
    public static void gather(String source) {
        findError(source);
    }

    public static void print() {
        Integer other = screenStat.get(ALL) - screenStat.get(FAILURE) - screenStat.get(SUCCESS);

        screenStat.put(OTHER, other);
        screenStat.computeIfPresent(FAILURE, (x, y) -> y + other);

        System.out.println(screenStat);
    }

    /**
     * Increment certain stats value.
     *
     * @param item stats value
     */
    public static void increment(ScreenStatEnum item) {
        screenStat.computeIfPresent(item, (x, y) -> ++y);
        if (errorStateList.contains(item))
            increment(FAILURE);
    }

    private static void findError(String source) {
        Matcher mather;

        mather = Pattern.compile("Could not find codec").matcher(source);
        if (mather.find()) {
            increment(NOT_FOUND_CODEC_ERROR);
            return;
        }
        mather = Pattern.compile("Invalid data found").matcher(source);
        if (mather.find()) {
            increment(INVALID_DATA_FOUND);
            return;
        }
        mather = Pattern.compile("401 Unauthorized").matcher(source);
        if (mather.find()) {
            increment(WRONG_AUTH_ERROR);
            return;
        }
        mather = Pattern.compile("404 Stream Not Found").matcher(source);
        if (mather.find()) {
            increment(STREAM_NOT_FOUND);
            return;
        }
        mather = Pattern.compile("400 Bad Request").matcher(source);
        if (mather.find()) {
            increment(BAD_REQUEST);
            return;
        }
    }

}