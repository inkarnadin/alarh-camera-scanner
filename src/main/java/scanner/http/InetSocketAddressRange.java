package scanner.http;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Standard IP addresses range container.
 *
 * @author inkarnadin
 */
public class InetSocketAddressRange {

    private final LinkedList<InetSocketAddress> range = new LinkedList<>();

    /**
     * Add ip address to range list.
     *
     * @param address target ip
     */
    public void add(InetSocketAddress address) {
        range.add(address);
    }

    /**
     * Get all ip addresses by range.
     *
     * @return target ip list
     */
    public List<InetSocketAddress> list() {
        return range;
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
        return String.format("%s - %s", range.getFirst().getHostString(), range.getLast().getHostString());
    }

}