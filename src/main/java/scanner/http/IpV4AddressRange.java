package scanner.http;

import scanner.Preferences;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Standard IP addresses range container.
 *
 * @author inkarnadin
 */
public class IpV4AddressRange {

    private static final int port = Integer.parseInt(Preferences.get("-p"));

    private final LinkedList<IpV4Address> range = new LinkedList<>();

    /**
     * Add ip address to range list.
     *
     * @param address target ip
     */
    public void add(IpV4Address address) {
        range.add(address);
    }

    /**
     * Get all ip addresses by range as {@code InetSocketAddress.class} list.
     *
     * @return target ip list
     */
    public List<InetSocketAddress> list() {
        return range.stream()
                .map(ip -> new InetSocketAddress(ip.toString(), port))
                .collect(Collectors.toList());
    }

    /**
     * Get first ip in range.
     *
     * @return first target ip
     */
    public IpV4Address first() {
        return range.getFirst();
    }

    /**
     * Get range ip count.
     *
     * @return count ip by range.
     */
    public int size() {
        return range.size();
    }

    /**
     * Present ip range as string with first and last addresses by range.
     *
     * @return string presentation of range
     */
    @Override
    public String toString() {
        return String.format("%s-%s", range.getFirst().toString(), range.getLast().toString());
    }

}