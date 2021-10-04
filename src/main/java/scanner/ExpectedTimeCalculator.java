package scanner;

import lombok.extern.slf4j.Slf4j;

/**
 * Class for calculating the expected scan execution time.
 *
 * @author inkarnadin
 */
@Slf4j
public class ExpectedTimeCalculator {

    private static final double avgOneAddressScanCoeff = 0.035d;
    private static final double avgOneAddressBruteCoeff = 0.5d;

    /**
     * Calculate and get expected time in milliseconds.
     *
     * @param addressCount count of addresses
     * @return time in milliseconds
     */
    public static Long expectedTime(long addressCount) {
        double expectedTime = (avgOneAddressScanCoeff * addressCount) + (addressCount * avgOneAddressBruteCoeff) * 1000;
        return Double.valueOf(expectedTime).longValue();
    }

}