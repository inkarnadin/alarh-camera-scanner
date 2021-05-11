package scanner.http;

import lombok.Getter;
import scanner.Preferences;
import scanner.recover.RecoveryManager;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static scanner.recover.RecoveryElement.*;

/**
 * Range manager class.
 *
 * @author inkarnadin
 */
public class RangeManager {

    @Getter
    private static final List<InetSocketAddressRange> addressCache = new ArrayList<>();

    private static final int port = Integer.parseInt(Preferences.get("-p"));
    private static boolean isRecovered = Preferences.check("-recovery_scanning");

    private static final String stopScanAddress = RecoveryManager.getRestoredValue(STOP_SCAN_POINT);

    /**
     * The method receives a textual representation of the range in the <b>10.20.3.0-10.20.4.255</b> format,
     * which is then split into separate addresses and stored in {@code addressCache} variable as a list.
     *
     * <p> If the range is too large (the second significant digits of the start and end are different),
     * it is split into sub-ranges, which will also be presented as lists of end addresses.
     *
     * <p> If the first significant digit of the beginning and end of the range is different,
     * then it is skipped (too large, processing will take a long time).
     *
     * <p> Ranges that have already been checked are skipped, that is, they were in the list before the address
     * at which the check was interrupted and which was saved (the starting address of the range or sub-range,
     * not the exact address of the stop).
     *
     * <p> As soon as the method receives the range in which the check was paused,
     * the restore flag is cleared and the ranges are considered active.
     *
     * @param rangeAsString range of IPs in string view, ex. <b>10.20.3.0-10.20.4.255</b>
     */
    public static void prepare(String rangeAsString) {
        InetSocketAddressRange resultRange = new InetSocketAddressRange();
        RangeSplitter rangeContainer = new RangeSplitter(rangeAsString);
        List<IpV4Address> range = rangeContainer.disassembleRange();

        if (needRestore(range))
            return;

        for (IpV4Address address : range)
            resultRange.add(new InetSocketAddress(address.toString(), port));
        addressCache.add(resultRange);
    }

    /**
     * Get count of all IP addresses by all ranges.
     *
     * The total number of addresses contained in each range is calculated.
     *
     * @return count of IP addresses
     */
    public static long count() {
        return addressCache.stream()
                .map(InetSocketAddressRange::size)
                .reduce(0, Integer::sum);
    }

    private static boolean needRestore(List<IpV4Address> range) {
        if (isRecovered)
            isRecovered = Objects.nonNull(stopScanAddress) && !range.contains(new IpV4Address(stopScanAddress));
        return isRecovered;
    }

}