package scanner.http;

import lombok.Getter;
import scanner.Preferences;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Range manager class.
 *
 * @author inkarnadin
 */
public class RangeManager {

    @Getter
    private static final List<InetSocketAddressRange> addressCache = new ArrayList<>();

    private final static int port = Integer.parseInt(Preferences.get("-p"));

    /**
     * Prepare scanning ranges.
     *
     * @param rangeAsString range of IPs in string view, ex. 10.20.3.0-10.20.4.255
     */
    public static void prepareSinglePortScanning(String rangeAsString) {
        InetSocketAddressRange resultRange = new InetSocketAddressRange();
        IpV4Range rangeContainer = new IpV4Range(rangeAsString);
        List<IpV4Address> range = rangeContainer.disassembleRange();
        for (IpV4Address address : range)
            resultRange.add(new InetSocketAddress(address.toString(), port));
        addressCache.add(resultRange);
    }

    /**
     * Get count of all IP addresses by all ranges.
     *
     * @return count of IP addresses
     */
    public static long count() {
        return addressCache.stream()
                .map(InetSocketAddressRange::size)
                .reduce(0, Integer::sum);
    }

}