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
public enum ScanStatItem {

    TOTAL { @Override public String toString() { return "Total ip scanned"; } },
    RANGES { @Override public String toString() { return "Scanned range"; } },
    LARGE_RANGES { @Override public String toString() { return "Scanned large range"; } },
    SUCCESS { @Override public String toString() { return "Success scanned"; } },
    FAILURE { @Override public String toString() { return "Failure scanned"; } }

}