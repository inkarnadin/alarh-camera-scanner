package scanner;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class CameraScanExecutor implements Callable<Optional<String>> {

    @Setter
    private int timeout = 500;

    private final InetSocketAddress address;

    @Override
    public Optional<String> call() {
        try (Socket socket = new Socket()) {
            log.debug("{} checked", address);
            socket.connect(address, timeout);
            socket.close();
            return Optional.of(address.getAddress().toString().replace("/", ""));
        } catch (IOException ignored) {}
        return Optional.empty();
    }

}