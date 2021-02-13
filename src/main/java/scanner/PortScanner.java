package scanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PortScanner {

    private static final int timeout = 1000;

    private final List<InetSocketAddress> addresses = new ArrayList<>();
    private final Converter converter = new Converter();

    private final List<String> result = new ArrayList<>();

    public void prepareSinglePortScanning(String rangeAsString, int port) {
        addresses.clear();
        IpV4Range rangeContainer = new IpV4Range(rangeAsString);
        List<IpV4Address> range = rangeContainer.range();
        for (IpV4Address address : range)
            addresses.add(converter.convert(address, port));
    }

    public void scanning() {
        for (InetSocketAddress address : addresses) {
            try (Socket socket = new Socket()) {
                socket.connect(address, timeout);
                System.out.println(address.getHostName());
            } catch (IOException ignored) {}
        }
    }

    public void simpleScan(InetSocketAddress host) {
        try (Socket socket = new Socket()) {
            socket.connect(host, timeout);
            System.out.println(host.getHostName());
        } catch (IOException ignored) {}
    }

}
