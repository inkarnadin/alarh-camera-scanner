package scanner.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration kind of statistic strings for scanning.
 *
 * @author inkarnadin
 */
@Getter
@RequiredArgsConstructor
public enum ScanStatEnum {

    ALL(0),

    RANGES(1),
    LARGE_RANGES(1),

    SUCCESS(2),
    FAILURE(2);

    private final int order;

}