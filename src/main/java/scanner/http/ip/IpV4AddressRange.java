package scanner.http.ip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Class-container for store and control ip range state.
 *
 * @author inkarnadin
 * on 31-05-2022
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class IpV4AddressRange {

    private String sourceRange;
    private BigInteger startIp;
    private BigInteger stopIp;
    private long count;
    private boolean isLarge;

    @SneakyThrows
    public IpV4AddressRange(String range) {
        sourceRange = range;
        String[] rangeAddresses = range.split("-");

        byte[] start = InetAddress.getByName(rangeAddresses[0]).getAddress();
        startIp = new BigInteger(1, start);

        byte[] stop = InetAddress.getByName(rangeAddresses[1]).getAddress();
        stopIp = new BigInteger(1, stop);

        count = stopIp.subtract(startIp).longValue();
        if (count > 255) {
            isLarge = true;
        }
    }

    /**
     * Method get list of all range ip addresses by range.
     *
     * @return list of ip addresses
     */
    public Set<InetSocketAddress> getAddresses() {
        Set<InetSocketAddress> results = new HashSet<>();
        BigInteger inc = new BigInteger("1");

        results.add(getIp());
        while (!Objects.equals(startIp, stopIp)) {
            startIp = startIp.add(inc);
            InetSocketAddress inetSocketAddress = getIp();
            results.add(inetSocketAddress);
        }
        return results;
    }

    /**
     * Method get first range IP address.
     *
     * @return IP address as string
     */
    public String first() {
        return sourceRange.split("-")[0];
    }

    @SneakyThrows
    public boolean contains(String ip) {
        BigInteger currentIp = new BigInteger(1, InetAddress.getByName(ip).getAddress());
        return (startIp.compareTo(currentIp) < 0 && stopIp.compareTo(currentIp) > 0);
    }

    /**
     * Method get {@link InetSocketAddress} from active IP value.
     * If IP as bytes arrays has more 4 bytes (leading sign byte), it truncated leading byte because IP address always positive.
     *
     * @return active {@link InetSocketAddress}
     */
    @SneakyThrows
    private InetSocketAddress getIp() {
        try {
            byte[] startIpBytes = startIp.toByteArray();
            byte[] truncatedBytes = (startIpBytes.length == 5)
                    ? Arrays.copyOfRange(startIpBytes, 1, 5)
                    : startIpBytes;
            return new InetSocketAddress(InetAddress.getByAddress(truncatedBytes), 554);
        } catch (Exception xep) {
            log.warn("wrong byte array of IP address: {}", xep.getMessage());
            throw xep;
        }
    }

}