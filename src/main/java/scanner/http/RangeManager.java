package scanner.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.recover.RecoveryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static scanner.Preferences.ALLOW_RECOVERY_SCANNING;
import static scanner.recover.RecoveryElement.STOP_SCAN_POINT;

/**
 * Range manager parsing class.
 *
 * @author inkarnadin
 */
@Slf4j
public class RangeManager {

    private final static int min = 0;
    private final static int max = 255;

    private final static int limitCount = 30_000_000;

    @Getter
    private static final List<IpV4AddressRange> addressCache = new ArrayList<>();

    private static boolean isRecovered = Preferences.check(ALLOW_RECOVERY_SCANNING);

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
        log.debug("processing range {}", rangeAsString);

        IpV4AddressRange resultRange = new IpV4AddressRange();
        List<IpV4Address> range = parseRange(rangeAsString);

        if (needRestore(range)) {
            log.debug("need restore flag is active, prepare this range was skipped");
            return;
        }

        for (IpV4Address address : range)
            resultRange.add(address);

        log.debug("current addresses count in cache list: {}", count());
        addressCache.add(resultRange);
    }

    /**
     * The method for checking if the number of addresses is exceeded. If the limit is exceeded, there is a risk of
     * receiving a memory overflow error with standard JVM settings (heap size).
     *
     * In order to avoid problems, the application ends its work with code 2.
     *
     * The current limit is 30 million addresses at a time.
     *
     * @param ranges list of all ranges
     */
    public static void validateMaxAddressesCount(List<String> ranges) {
        long totalCount = 0;
        for (String rangeAsString : ranges) {
            totalCount += parseRange(rangeAsString).size();
        }

        log.debug("total IP addresses count {}", totalCount);

        if (totalCount > limitCount) {
            log.warn("addresses limit count was overflowed - total {}, expected {}", totalCount, limitCount);
            System.exit(2);
        }
    }

    /**
     * Get count of all IP addresses by all ranges.
     * The total number of addresses contained in each range is calculated.
     *
     * @return count of IP addresses
     */
    public static long count() {
        return addressCache.stream()
                .map(IpV4AddressRange::size)
                .reduce(0, Integer::sum);
    }

    private static boolean needRestore(List<IpV4Address> range) {
        if (isRecovered)
            isRecovered = Objects.nonNull(stopScanAddress) && !range.contains(new IpV4Address(stopScanAddress));
        return isRecovered;
    }

    private static List<IpV4Address> parseRange(String range) {
        try {
            String[] rangeAddresses = range.split("-");

            IpV4Address startAddress;
            IpV4Address endAddress;

            if (rangeAddresses.length == 1) {
                startAddress = new IpV4Address(rangeAddresses[0]);
                endAddress = new IpV4Address(rangeAddresses[0]);
            } else {
                startAddress = new IpV4Address(rangeAddresses[0]);
                endAddress = new IpV4Address(rangeAddresses[1]);
            }

            int startPart1 = startAddress.getPart1();
            int startPart2 = startAddress.getPart2();
            int startPart3 = startAddress.getPart3();
            int startPart4 = startAddress.getPart4();

            int endPart3 = endAddress.getPart3();

            if (startPart1 != endAddress.getPart1()) {
                log.warn("{} - {}. This range too large and will be skipped", startAddress.toString(), endAddress.toString());
                return new ArrayList<>();
            }

            if (startPart2 != endAddress.getPart2())
                return parseLargeRange(startAddress, endAddress);

            List<IpV4Address> addresses = new ArrayList<>();
            while (startPart3 <= endPart3) {
                int endPart4 = (startPart3 != endPart3) ? max : endAddress.getPart4();
                while (startPart4 <= endPart4) {
                    IpV4Address ipV4Address = new IpV4Address(startPart1, startPart2, startPart3, startPart4);
                    addresses.add(ipV4Address);

                    startPart4++;
                }
                startPart3++;
                startPart4 = min;
            }
            return addresses;
        } catch (Exception xep) {
            log.error("error during calculation range {}, message {}", range, xep.getMessage());
        }
        return new ArrayList<>();
    }

    private static List<IpV4Address> parseLargeRange(IpV4Address startAddress, IpV4Address endAddress) {
        log.trace("start processing large range {} - {}", startAddress.getIpAsString(),endAddress.getIpAsString());

        String rangeAsString = new StringBuilder()
                .append(startAddress.toString())
                .append("-")
                .append(new IpV4Address(startAddress.getPart1(), startAddress.getPart2(), max, max).toString())
                .toString();
        List<IpV4Address> range = new ArrayList<>(parseRange(rangeAsString));

        for (int i = 1; i < endAddress.getPart2() - startAddress.getPart2(); i++) {
            rangeAsString = new StringBuilder()
                    .append(new IpV4Address(startAddress.getPart1(), startAddress.getPart2() + i, min, min).toString())
                    .append("-")
                    .append(new IpV4Address(startAddress.getPart1(), startAddress.getPart2() + i, max, max).toString())
                    .toString();
            range.addAll(parseRange(rangeAsString));
        }

        rangeAsString = new StringBuilder()
                .append(new IpV4Address(endAddress.getPart1(), endAddress.getPart2(), min, min).toString())
                .append("-")
                .append(endAddress.toString())
                .toString();
        range.addAll(parseRange(rangeAsString));

        log.trace("stop processing large range {} - {}", startAddress.getIpAsString(),endAddress.getIpAsString());

        return range;
    }

}