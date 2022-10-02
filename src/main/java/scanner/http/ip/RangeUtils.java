package scanner.http.ip;

import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.recover.RecoveryManager;

import java.util.*;

import static scanner.Preferences.ALLOW_RECOVERY_SCANNING;
import static scanner.recover.RecoveryElement.STOP_SCAN_RANGE;

/**
 * Range manager parsing class.
 *
 * @author inkarnadin
 * on 11.05.2021
 */
@Slf4j
public final class RangeUtils {

    private RangeUtils() {
        throw new AssertionError("Utility class can't be instantiate");
    }

    /**
     * Method prepare IP range list.
     *
     * @param listSources list of IP addresses as string
     */
    public static Set<IpV4AddressRange> prepare(Set<String> listSources) {
        log.info("count of ranges: {}", listSources.size());
        Set<IpV4AddressRange> result = new TreeSet<>();
        for (String rangeAsString : listSources) {
            log.trace("prepare range: {}", rangeAsString);
            IpV4AddressRange ipV4AddressRange = new IpV4AddressRange(rangeAsString);

            if (result.isEmpty() && needRestore(ipV4AddressRange)) {
                log.debug("need restore flag is active, prepare this range was skipped");
                continue;
            }
            result.add(ipV4AddressRange);
        }

        log.info("total addresses for scanning: {}", RangeUtils.count(result));
        return result;
    }

    public static long count(Set<IpV4AddressRange> ranges) {
        return ranges.stream()
                .mapToLong(IpV4AddressRange::getCount)
                .sum();
    }

    /**
     * Method checks if the previous scan session needs to be restored.
     * <p>If the scan flag is set to {@code true}, then it checks that the session ended on the specified range
     *
     * @param range checking range
     * @return state, if {@code true} - need restore, else not
     */
    private static boolean needRestore(IpV4AddressRange range) {
        if (Preferences.check(ALLOW_RECOVERY_SCANNING)) {
            String stopScanAddress = RecoveryManager.getRestoredValue(STOP_SCAN_RANGE);
            if (Objects.nonNull(stopScanAddress)) {
                return !Objects.equals(range.getSourceRange(), stopScanAddress);
            }
        }
        return false;
    }

}