package scanner;

import java.net.InetSocketAddress;

public class Converter {

    InetSocketAddress convert(IpV4Address ip, int port) {
        return new InetSocketAddress(ip.getIpAsString(), port);
    }

}
