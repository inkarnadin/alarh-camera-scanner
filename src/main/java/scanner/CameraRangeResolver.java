package scanner;

import lombok.Getter;
import scanner.http.IpV4Address;
import scanner.http.IpV4Range;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class CameraRangeResolver {

    @Getter
    private static final List<List<InetSocketAddress>> addressCache = new ArrayList<>();

    private final static int port = Integer.parseInt(Preferences.get("-p"));

    /**
     * Prepare scanning ranges.
     *
     * @param rangeAsString range of IPs in string view, ex. 10.20.3.0-10.20.4.255
     */
    public static void prepareSinglePortScanning(String rangeAsString) {
        List<InetSocketAddress> addresses = new ArrayList<>();
        IpV4Range rangeContainer = new IpV4Range(rangeAsString);
        List<IpV4Address> range = rangeContainer.range();
        for (IpV4Address address : range)
            addresses.add(new InetSocketAddress(address.getIpAsString(), port));
        addressCache.add(addresses);
    }

    /**
     * Get count of all IP addresses by all ranges.
     *
     * @return count of IP addresses
     */
    public static long count() {
        return addressCache.stream()
                .map(List::size)
                .reduce(0, Integer::sum);
    }

}