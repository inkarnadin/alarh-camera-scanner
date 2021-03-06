package scanner.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class IpV4Range {

    private final static int min = 0;
    private final static int max = 255;

    private final IpV4Address startAddress;
    private final IpV4Address endAddress;

    public IpV4Range(String range) {
        String[] rangeAddresses = range.split("-");

        if (rangeAddresses.length == 1) {
            startAddress = new IpV4Address(rangeAddresses[0]);
            endAddress = new IpV4Address(rangeAddresses[0]);
        } else {
            startAddress = new IpV4Address(rangeAddresses[0]);
            endAddress = new IpV4Address(rangeAddresses[1]);
        }
    }

    public List<IpV4Address> range() {
        int startPart1 = startAddress.getPart1();
        int startPart2 = startAddress.getPart2();
        int startPart3 = startAddress.getPart3();
        int startPart4 = startAddress.getPart4();

        int endPart3 = endAddress.getPart3();

        if (startPart1 != endAddress.getPart1() || startPart2 != endAddress.getPart2()) {
            log.warn("{} - {}. This range too large and will be skipped", startAddress.getIpAsString(), endAddress.getIpAsString());
            return new ArrayList<>();
        }

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
    }

}
