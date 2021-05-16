package scanner.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration kind of error and statistic strings for screen saving.
 *
 * @author inkarnadin
 */
@Getter
@RequiredArgsConstructor
public enum ScreenStatItem {

    ALL(0),
    SUCCESS(1),
    FAILURE(1),

    INVALID_DATA_FOUND(2),
    WRONG_AUTH_ERROR(2),
    NOT_FOUND_CODEC_ERROR(2),
    STREAM_NOT_FOUND(2),
    BAD_REQUEST(2),
    PROTOCOL_NOT_SUPPORTED(2),
    PARAMETER_NOT_UNDERSTOOD(2),
    UNEXPECTED_ERROR(2),
    OTHER(2);

    private final int order;

}