package scanner;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
public class CameraScanTask implements Runnable {

    @Setter
    private int timeout = 500;

    private final CompletableFuture<Optional<String>> future;
    private final InetSocketAddress address;

    @Override
    public void run() {
        Thread.currentThread().setName(String.format("brute-%s", address));
        try (Socket socket = new Socket()) {
            socket.connect(address, timeout);
            socket.close();
            log.debug("success checked");
            future.complete(Optional.of(address.getAddress().toString().replace("/", "")));
        } catch (Exception xep) {
            future.complete(Optional.empty());
        }
    }

}