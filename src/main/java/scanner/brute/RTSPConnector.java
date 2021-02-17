package scanner.brute;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class RTSPConnector {

    private final static String CRCL = "\r\n";
    private final static String SPACE = " ";
    private final static int PORT = 554;

    public static boolean describe(String ip, String login, String password) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, PORT));

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

            String statusLine = bufferedReader.readLine();
            return ("RTSP/1.0 200 OK".equals(statusLine));
        } catch (Exception xep) {
            log.warn(xep.getMessage());
        }
        return false;
    }

}