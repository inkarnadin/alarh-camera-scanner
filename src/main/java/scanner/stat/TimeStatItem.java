package scanner.stat;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TimeStatItem {

    TOTAL_TIME { @Override public String toString() { return "Total time"; } },
    EXPECTED_TIME { @Override public String toString() { return "Expected time"; } },
    TOTAL_SCAN_TIME { @Override public String toString() { return "Total scanning time"; } },
    TOTAL_BRUTE_TIME { @Override public String toString() { return "Total brute time"; } },
    AVG_SCAN_TIME { @Override public String toString() { return "Average address scanning time"; } },
    AVG_BRUTE_TIME { @Override public String toString() { return "Average address brute time"; } }

}