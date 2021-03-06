package scanner.rtsp;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Context;
import scanner.Preferences;
import scanner.brute.AuthState;
import scanner.brute.SocketConnector;
import scanner.brute.TransportMode;
import scanner.ffmpeg.FFmpegExecutor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

/**
 * RTSP credential pair checking class.
 *
 * @author inkarnadin
 */
@Slf4j
@NoArgsConstructor
public class RTSPCredentialVerifier implements Closeable {

    private final static int port = 554;

    private SocketConnector connector;
    private String ip;

    private final static int successCode = 200;
    private final static int failureCode = 401;
    private final static int notFoundCode = 404;
    private final static int sessionNotFoundCode = 454;
    private final static int badRequestCode = 400;
    private final static int invalidParamCode = 451;
    private final static int unknownStateCode = 418;

    private final static List<Integer> badCodes = Arrays.asList(notFoundCode, badRequestCode, invalidParamCode, unknownStateCode, sessionNotFoundCode);
    private final static List<Integer> goodCodes = Arrays.asList(successCode, failureCode);

    /**
     * Wrapper for create socket connection.
     * Try until the connection is established or attempts are terminated.
     *
     * @param ip target IP address.
     * @throws SocketException if connection failed.
     */
    public void connect(String ip) throws SocketException {
        this.ip = ip;
        int repeatCount = Integer.parseInt(Preferences.get("-a"));
        connector = new SocketConnector(ip, port);
        do {
            try {
                connector.open();
            } catch (IOException ignored) {
                log.debug("socket connection failed (try {})", repeatCount--);
            }
            if (connector.isConnected())
                return;
        } while (repeatCount > 0);

        log.warn("socket connection failed (attempts ended)");
        throw new SocketException();
    }

    /**
     * Wrapper for closing socket connection.
     */
    @Override
    public void close() {
        connector.close();
    }

    /**
     * Method of auth attempt via RTSP describe.
     *
     * @param credentials Login and password pair, ex. admin:12345.
     * @return Authentication status.
     */
    @SneakyThrows
    public AuthState check(String credentials) {
        try {
            RequestBuilder builder = new RequestBuilder(ip, credentials);

            int statusCode = send(builder.describe()).getCode();
            if (badCodes.contains(statusCode)) {
                Context.set(ip, TransportMode.SPECIAL);
                statusCode = send(builder.describe()).getCode();
                if (!goodCodes.contains(statusCode)) {
                    log.warn("skipped wrong request");
                    return AuthState.UNKNOWN_STATE;
                }
            }

            if (statusCode == successCode && Preferences.check("-screen"))
                FFmpegExecutor.saveFrame(credentials, ip);

            return statusCode == successCode
                    ? AuthState.AUTH
                    : AuthState.NOT_AUTH;
        } catch (IOException xep) {
            log.warn("ip not available");
            return AuthState.NOT_AVAILABLE;
        }
    }

    private RTSPStateStore send(String request) throws IOException {
        BufferedReader bufferedReader = connector.input();
        BufferedWriter bufferedWriter = connector.output();

        bufferedWriter.write(request);
        bufferedWriter.flush();

        RTSPStateStore store = ResponseHandler.handle(bufferedReader);
        log.debug("response => {}", store.getStatusLine());

        return store;
    }

}