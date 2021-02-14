package scanner;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PortExecutor implements Callable<Optional<String>> {

    private static final int timeout = 500;

    private final InetSocketAddress address;

    @Override
    public Optional<String> call() {
        try (Socket socket = new Socket()) {
            socket.connect(address, timeout);
            return Optional.ofNullable(address.getHostName());
        } catch (IOException ignored) {}
        return Optional.empty();
    }

}