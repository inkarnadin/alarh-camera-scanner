package scanner.stat;

import java.util.*;

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

    private static final Map<ScreenStatEnum, Integer> screenStats = new HashMap<>() {{
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
        screenStats.computeIfPresent(item, (x, y) -> ++y);
        if (errorStateList.contains(item))
            increment(FAILURE);
    }

    private static String normalize(ScreenStatEnum item) {
        return String.format("%s%s: %s", "\t".repeat(item.getOrder()), item, screenStats.get(item));
    }

    private static void recalculate() {
        Integer other = screenStats.get(ALL) - screenStats.get(FAILURE) - screenStats.get(SUCCESS);

        screenStats.put(OTHER, other);
        screenStats.computeIfPresent(FAILURE, (x, y) -> y + other);
    }

}