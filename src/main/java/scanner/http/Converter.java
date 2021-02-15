package scanner.http;

import java.net.InetSocketAddress;

public class Converter {

    public InetSocketAddress convert(IpV4Address ip, int port) {
        return new InetSocketAddress(ip.getIpAsString(), port);
    }

}
