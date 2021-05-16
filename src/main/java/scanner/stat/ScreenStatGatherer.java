package scanner.stat;

import java.util.*;
import java.util.stream.Collectors;

import static scanner.stat.ScreenStatItem.*;

/**
 * Screenshot save statistic class.
 *
 * @author inkarnadin
 */
public class ScreenStatGatherer {

    private static final List<ScreenStatItem> errorStateList = Arrays.asList(
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

    private static final Map<ScreenStatItem, Long> screenStats = new TreeMap<>() {{
        put(ALL, 0L);
        put(SUCCESS, 0L);
        put(FAILURE, 0L);

        put(INVALID_DATA_FOUND, 0L);
        put(WRONG_AUTH_ERROR, 0L);
        put(NOT_FOUND_CODEC_ERROR, 0L);
        put(STREAM_NOT_FOUND, 0L);
        put(BAD_REQUEST, 0L);
        put(PROTOCOL_NOT_SUPPORTED, 0L);
        put(PARAMETER_NOT_UNDERSTOOD, 0L);
        put(UNEXPECTED_ERROR, 0L);
        put(OTHER, 0L);
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
    public static void increment(ScreenStatItem item) {
        screenStats.computeIfPresent(item, (x, y) -> ++y);
        if (errorStateList.contains(item))
            increment(FAILURE);
    }

    /**
     * Set certain stats value.
     *
     * @param item stats value
     * @param value explicitly meaning
     */
    public static void set(ScreenStatItem item, long value) {
        screenStats.put(item, value);
    }

    /**
     * Get certain value.
     *
     * @param item stats value
     * @return value by key
     */
    public static Long get(ScreenStatItem item) {
        return screenStats.getOrDefault(item, 0L);
    }

    /**
     * Get all statistic values in natural ordered with splitter.
     *
     * @return all values as string
     */
    public static String getStatsAsString() {
        recalculate();
        return screenStats.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(";"));
    }

    private static String normalize(ScreenStatItem item) {
        return String.format("%s%s: %s", "\t".repeat(item.getOrder()), item, screenStats.get(item));
    }

    private static void recalculate() {
        Long other = screenStats.get(ALL) - screenStats.get(FAILURE) - screenStats.get(SUCCESS);

        screenStats.put(OTHER, other);
        screenStats.computeIfPresent(FAILURE, (x, y) -> y + other);
    }

}