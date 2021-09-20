package scanner.stat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static scanner.stat.ScreenStatItem.*;

/**
 * Screenshot save statistic class.
 *
 * @author inkarnadin
 */
public class ScreenStatGatherer extends AbstractStatGatherer<ScreenStatItem, Long> {

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

    public ScreenStatGatherer() {
        data.put(ALL, 0L);
        data.put(SUCCESS, 0L);
        data.put(FAILURE, 0L);

        data.put(INVALID_DATA_FOUND, 0L);
        data.put(WRONG_AUTH_ERROR, 0L);
        data.put(NOT_FOUND_CODEC_ERROR, 0L);
        data.put(STREAM_NOT_FOUND, 0L);
        data.put(BAD_REQUEST, 0L);
        data.put(PROTOCOL_NOT_SUPPORTED, 0L);
        data.put(PARAMETER_NOT_UNDERSTOOD, 0L);
        data.put(UNEXPECTED_ERROR, 0L);
        data.put(OTHER, 0L);
    }

    /**
     * Increment certain stats value.
     *
     * @param item stats value
     */
    @Override
    public void increment(ScreenStatItem item) {
        data.computeIfPresent(item, (x, y) -> ++y);
        if (errorStateList.contains(item))
            increment(FAILURE);
    }

    /**
     * Not used
     *
     * @param item stats value
     * @param value by key
     */
    @Override
    public void incrementBy(ScreenStatItem item, Long value) {}

    /**
     * Get certain value.
     *
     * @param item stats value
     * @return value by key
     */
    public Long get(ScreenStatItem item) {
        return data.getOrDefault(item, 0L);
    }

    /**
     * Get all statistic values in natural ordered with splitter.
     *
     * @return all values as string
     */
    public String getStatsAsString() {
        recalculate();
        return data.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(";"));
    }

    protected void recalculate() {
        Long other = data.get(ALL) - data.get(FAILURE) - data.get(SUCCESS);

        data.put(OTHER, other);
        data.computeIfPresent(FAILURE, (x, y) -> y + other);
    }

}