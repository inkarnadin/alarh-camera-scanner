package scanner.stat;

import java.time.LocalTime;
import java.util.*;
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
            PROTOCOL_NOT_SUPPORTED,
            PARAMETER_NOT_UNDERSTOOD,
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
        put(PROTOCOL_NOT_SUPPORTED, 0);
        put(PARAMETER_NOT_UNDERSTOOD, 0);
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

    /**
     * Create full report by gather statistic data.
     *
     * @return formatted report data
     */
    public static String createReport() {
        recalculate();

        return new StringJoiner("\n")
                .add("Screen save stats =============================")
                .add(normalize(ALL))
                .add(normalize(SUCCESS))
                .add(normalize(FAILURE))
                .add(normalize(INVALID_DATA_FOUND))
                .add(normalize(WRONG_AUTH_ERROR))
                .add(normalize(NOT_FOUND_CODEC_ERROR))
                .add(normalize(STREAM_NOT_FOUND))
                .add(normalize(BAD_REQUEST))
                .add(normalize(PROTOCOL_NOT_SUPPORTED))
                .add(normalize(PARAMETER_NOT_UNDERSTOOD))
                .add(normalize(UNEXPECTED_ERROR))
                .add(normalize(OTHER))
                .toString();
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

    private static String normalize(ScreenStatEnum item) {
        return String.format("%s%s: %s", "\t".repeat(item.getOrder()), item, screenStat.get(item));
    }

    private static void recalculate() {
        Integer other = screenStat.get(ALL) - screenStat.get(FAILURE) - screenStat.get(SUCCESS);

        screenStat.put(OTHER, other);
        screenStat.computeIfPresent(FAILURE, (x, y) -> y + other);
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
        mather = Pattern.compile("461 Unkown").matcher(source);
        if (mather.find()) {
            increment(PROTOCOL_NOT_SUPPORTED);
            return;
        }
        mather = Pattern.compile("451 Parameter Not Understood").matcher(source);
        if (mather.find()) {
            increment(PARAMETER_NOT_UNDERSTOOD);
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