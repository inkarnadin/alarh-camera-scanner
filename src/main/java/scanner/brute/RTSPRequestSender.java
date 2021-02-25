package scanner.brute;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.Context;
import scanner.Preferences;

import java.io.*;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RTSP request sending class.
 * Only supports DESCRIBE method sufficient to iterate over.
 *
 * @author inkarnadin
 */
@Slf4j
@NoArgsConstructor
public class RTSPRequestSender implements Closeable {

    private final static String crcl = "\r\n";
    private final static String space = " ";

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
    public AuthState describe(String credentials) {
        try {
            int statusCode = send(request(ip, credentials));

            if (badCodes.contains(statusCode)) {
                Context.set(ip, RTSPMode.SPECIAL);
                statusCode = send(request(ip, credentials));
                if (!goodCodes.contains(statusCode)) {
                    log.warn("skipped wrong request");
                    return AuthState.UNKNOWN_STATE;
                }
            }

            return statusCode == successCode
                    ? AuthState.AUTH
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

    private int send(String request) throws IOException {
        BufferedReader bufferedReader = connector.input();
        BufferedWriter bufferedWriter = connector.output();

        bufferedWriter.write(request);
        bufferedWriter.flush();

        String statusLine = bufferedReader.readLine();
        log.debug("response => {}", statusLine);

        bufferedReader.mark(0);
        bufferedReader.reset();

        Matcher matcher = Pattern.compile("RTSP/1\\.0\\s(\\d{3})").matcher(statusLine);
        return matcher.find()
                ? Integer.parseInt(matcher.group(1))
                : unknownStateCode;
    }

}