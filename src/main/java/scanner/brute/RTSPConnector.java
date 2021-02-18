package scanner.brute;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CancellationException;

@Slf4j
public class RTSPConnector {

    private final static String CRCL = "\r\n";
    private final static String SPACE = " ";

    private final static int PORT = 554;
    private final static int TIMEOUT = 1000;

    private final static String success = "RTSP/1.0 200 OK";
    private final static String failure = "RTSP/1.0 401 Unauthorized";
    private final static String not_found = "RTSP/1.0 404 Not Found";

    @SneakyThrows
    public static AuthState describe(String ip, String login, String password) {
        String statusLine = "";
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, PORT), TIMEOUT);

            Sender sender = new Sender(socket);
            RTSPBuilder builder = new RTSPBuilder(login, password, ip);

            statusLine = sender.send(builder.baseRequest);
            if (not_found.equals(statusLine))
                statusLine = sender.send(builder.baseRequestNotFound);

           switch (statusLine) {
               case success: return AuthState.AUTH;
               case failure: return AuthState.NOT_AUTH;
               case not_found: default: return AuthState.NOT_AVAILABLE;
           }
        } catch (SocketTimeoutException ste) {
            throw new CancellationException();
        } catch (IOException xep) {
            log.warn("ip={}: {}/{}", ip, xep.getMessage(), statusLine);
            return AuthState.NOT_AVAILABLE;
        }
    }

    @RequiredArgsConstructor
    private static class RTSPBuilder {

        @Getter
        private final String baseRequest;
        @Getter
        private final String baseRequestNotFound;

        RTSPBuilder(String login, String password, String ip) {
            baseRequest = new StringBuilder()
                    .append("DESCRIBE").append(SPACE).append("rtsp://").append(login).append(":").append(password).append("@").append(ip).append(":").append(PORT)
                    .append(SPACE).append("RTSP/1.0").append(CRCL)
                    .append("CSeq:").append(SPACE).append("1").append(CRCL)
                    .append("Content-Type:").append(SPACE).append("application/sdp").append(CRCL)
                    .append(CRCL)
                    .toString();
            baseRequestNotFound = new StringBuilder()
                    .append("DESCRIBE").append(SPACE).append("rtsp://").append(login).append(":").append(password).append("@").append(ip).append(":").append(PORT)
                    .append("/Streaming/Channels/101").append(SPACE).append("RTSP/1.0").append(CRCL)
                    .append("CSeq:").append(SPACE).append("1").append(CRCL)
                    .append("Content-Type:").append(SPACE).append("application/sdp").append(CRCL)
                    .append(CRCL)
                    .toString();
        }
    }

    private static class Sender {

        private final String ip;
        private final Socket socket;

        @SneakyThrows
        public Sender(Socket socket) {
            this.ip = socket.getInetAddress().getHostAddress();
            this.socket = socket;
        }

        @SneakyThrows
        private String send(String request) {
            // FIXME its bad code
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bufferedWriter.write(request);
            bufferedWriter.flush();

            String statusLine = bufferedReader.readLine();
            log.debug("{} : {}", ip, statusLine);

            return statusLine;
        }

    }

}