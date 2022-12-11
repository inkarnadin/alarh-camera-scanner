package scanner.runner.exploring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static scanner.Preferences.SOCKET_TIMEOUT;

/**
 * Simple scanning subtask class.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class CameraScanTask implements Runnable {

    private final static int TIMEOUT = Integer.parseInt(Preferences.get(SOCKET_TIMEOUT));

    private final CompletableFuture<Optional<String>> future;
    private final InetSocketAddress address;

    /**
     * Start checking certain IP address.
     */
    @Override
    public void run() {
        Thread.currentThread().setName(String.format("scan-%s", address));
        try (Socket socket = new Socket()) {
            socket.connect(address, TIMEOUT);
            socket.close();
            log.trace("success checked, {}", address);
            future.complete(Optional.of(address.getAddress().toString().replace("/", "")));
        } catch (Exception xep) {
            future.complete(Optional.empty());
        }
    }

}