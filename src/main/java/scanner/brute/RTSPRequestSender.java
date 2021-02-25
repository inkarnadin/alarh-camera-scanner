package scanner.brute;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Context;
import scanner.Preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@NoArgsConstructor
public class RTSPRequestSender implements Closeable {

    private final static String crcl = "\r\n";
    private final static String space = " ";

    private final static int port = 554;

    private SocketConnector connector;
    private String ip;

    private final static String success = "RTSP/1.0 200 OK";
    private final static String failure = "RTSP/1.0 401"; // Unauthorized, Authorization Required
    private final static String not_found = "RTSP/1.0 404"; // Error, Not Found
    private final static String session_not_found = "RTSP/1.0 454"; // Session Not Found
    private final static String bad_request = "RTSP/1.0 400"; // Bad request
    private final static String invalid_param = "RTSP/1.0 451"; // Parameter Not Understood, Invalid Parameter
    private final static String internal = "RTSP/1.0 500"; // Internal Server Error
    private final static String unknown = "RTSP/1.0 418"; // null

    /**
     * Wrapper for create socket connection.
     * Try until the connection is established or attempts are terminated.
     *
     * @param ip - target IP address
     */
    @SneakyThrows
    public void connect(String ip) {
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
     * @param credentials - login and password pair, ex. admin:12345
     * @return - auth status
     */
    public AuthState describe(String credentials) {
        try {
            String statusLine = send(request(ip, credentials));

            if (statusLine.contains(not_found) || statusLine.contains(internal) || statusLine.contains(unknown)) {
                Context.set(ip, RTSPMode.SPECIAL);
                statusLine = send(request(ip, credentials));
            }

            return statusLine.equals(success)
                    ? AuthState.AUTH
                    : statusLine.contains(session_not_found) || statusLine.contains(bad_request) || statusLine.contains(invalid_param) || statusLine.equals(unknown)
                        ? AuthState.UNKNOWN_STATE
                        : AuthState.NOT_AUTH;
        } catch (IOException xep) {
            log.warn("ip not available");
            return AuthState.NOT_AVAILABLE;
        }
    }

    private String request(String ip, String credentials) {
        credentials = Objects.nonNull(credentials) ? credentials + "@" : "";
        return Context.get(ip) == RTSPMode.ORTHODOX
                ? new StringBuilder()
                    .append("DESCRIBE").append(space).append("rtsp://").append(credentials).append(ip).append(":").append(port)
                    .append("/").append(space).append("RTSP/1.0").append(crcl)
                    .append("CSeq:").append(space).append("1").append(crcl)
                    .append("Content-Type:").append(space).append("application/sdp").append(crcl)
                    .append(crcl)
                    .toString()
                : new StringBuilder()
                    .append("DESCRIBE").append(space).append("rtsp://").append(credentials).append(ip).append(":").append(port)
                    .append("/Streaming/Channels/101").append(space).append("RTSP/1.0").append(crcl)
                    .append("CSeq:").append(space).append("1").append(crcl)
                    .append("Content-Type:").append(space).append("application/sdp").append(crcl)
                    .append(crcl)
                    .toString();
    }

    private String send(String request) throws IOException {
        BufferedReader bufferedReader = connector.input();
        BufferedWriter bufferedWriter = connector.output();

        bufferedWriter.write(request);
        bufferedWriter.flush();

        String statusLine = bufferedReader.readLine();
        log.debug("response => {}", statusLine);

        bufferedReader.mark(0);
        bufferedReader.reset();

        return Objects.nonNull(statusLine) ? statusLine : "RTSP/1.0 418 Null";
    }

}