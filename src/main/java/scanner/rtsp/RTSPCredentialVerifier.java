package scanner.rtsp;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.runner.breaking.AuthState;
import scanner.ffmpeg.FFMpegExecutor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

import static scanner.Preferences.CONNECTION_ATTEMPT;
import static scanner.Preferences.PORT;

/**
 * Класс проверки валидности учетных данных по протоколу RSTP.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
@Slf4j
@NoArgsConstructor
public class RTSPCredentialVerifier implements Closeable {

    private final static int port = Integer.parseInt(Preferences.get(PORT));

    private SocketConnector connector;
    private String ip;

    private final static int SUCCESS_CODE = 200;
    private final static int FAILURE_CODE = 401;
    private final static int NOT_FOUND_CODE = 404;
    private final static int SESSION_NOT_FOUND_CODE = 454;
    private final static int BAD_REQUEST_CODE = 400;
    private final static int INVALID_PARAM_CODE = 451;
    private final static int UNKNOWN_STATE_CODE = 418;

    private final static List<Integer> BAD_CODES = Arrays.asList(NOT_FOUND_CODE, BAD_REQUEST_CODE, INVALID_PARAM_CODE, UNKNOWN_STATE_CODE, SESSION_NOT_FOUND_CODE);
    private final static List<Integer> GOOD_CODES = Arrays.asList(SUCCESS_CODE, FAILURE_CODE);

    /**
     * Метод создания сокет-соединения.
     * <p>Будет предпринято указанное количество попыток открыть соединение, прежде чем операция прервется.
     *
     * @param ip целевой адрес
     * @throws SocketException в случае неудачной попытки соединения
     */
    public void connect(String ip) throws SocketException {
        this.ip = ip;
        int repeatCount = Integer.parseInt(Preferences.get(CONNECTION_ATTEMPT));
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
     * Метод закрытия сокет соединения.
     */
    @Override
    public void close() {
        connector.close();
    }

    /**
     * Метод попытки аутентификации через сокет соединение с помощью RTSP метода <b>DESCRIBE<b/>.
     *
     * @param credentials учетные данные в виде строки, разделенной двоеточием (например, <i>admin:12345<i/>)
     * @return статус аутентификации
     */
    @SneakyThrows
    public AuthState check(String credentials) {
        try {
            RequestBuilder request = new RequestBuilder(ip, credentials);

            int statusCode = send(request).getCode();
            if (BAD_CODES.contains(statusCode)) {
                RTSPContext.set(ip, TransportMode.SPECIAL);
                statusCode = send(request).getCode();
                if (!GOOD_CODES.contains(statusCode)) {
                    log.warn("skipped wrong request");
                    return AuthState.UNKNOWN_STATE;
                }
            }

            if (statusCode == SUCCESS_CODE && Preferences.check(Preferences.ALLOW_FRAME_SAVING)) {
                FFMpegExecutor.saveFrame(credentials, ip);
            }

            return statusCode == SUCCESS_CODE
                    ? AuthState.AUTH
                    : AuthState.NOT_AUTH;
        } catch (IOException xep) {
            log.warn("ip not available: {}", xep.getMessage());
            return AuthState.NOT_AVAILABLE;
        }
    }

    /**
     * Метод отправки сообщения через сокет-соединение.
     *
     * @param request тело запроса
     * @return разобранный объект ответа
     * @throws IOException ошибка ввода-вывода в случае превышения времени ожидания ответа от сервера
     */
    private RTSPResponse send(RequestBuilder request) throws IOException {
        BufferedReader bufferedReader = connector.input();
        BufferedWriter bufferedWriter = connector.output();

        bufferedWriter.write(request.describe());
        bufferedWriter.flush();

        RTSPResponse rtspResponse = ResponseHandler.handle(bufferedReader);
        log.debug("[{}] response code => {}", request.getCredentials(), rtspResponse.getStatusLine());

        return rtspResponse;
    }

}