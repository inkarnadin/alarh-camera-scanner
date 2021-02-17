package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RTSPConnector {

    private final static String CRCL = "\r\n";
    private final static String SPACE = " ";

    private final static int PORT = 554;
    private final static int TIMEOUT = 1000;

    @SneakyThrows
    public static boolean describe(String ip, String login, String password) {
        String statusLine = "";
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, PORT), TIMEOUT);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String request = new StringBuilder()
                    .append("DESCRIBE").append(SPACE).append("rtsp://").append(login).append(":").append(password).append("@").append(ip).append(":").append(PORT)
                    .append(SPACE).append("RTSP/1.0").append(CRCL)
                    .append("CSeq:").append(SPACE).append("1").append(CRCL)
                    .append("Content-Type:").append(SPACE).append("application/sdp").append(CRCL)
                    .append(CRCL)
                    .toString();

            bufferedWriter.write(request);
            bufferedWriter.flush();

            statusLine = bufferedReader.readLine();
            return ("RTSP/1.0 200 OK".equals(statusLine));
        } catch (SocketTimeoutException ste) {
            throw new CancellationException();
        } catch (IOException xep) {
            log.warn("ip={}: {}/{}", ip, xep.getMessage(), statusLine);
        }
        return false;
    }

}