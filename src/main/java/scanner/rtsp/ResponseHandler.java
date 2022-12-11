package scanner.rtsp;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс разбора ответа (состояние, основные заголовки) о статусе установки соединения через сокет.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
@Slf4j
public class ResponseHandler {

    private static final String eos = "";

    private static final String cSeq = "CSeq";
    private static final String wwwAuthenticate = "WWW-Authenticate";
    private static final String session = "Session";

    /**
     * Метод обработки входного потока данных
     *
     * @param reader входной поток данных
     * @return объект ответа
     */
    @SneakyThrows
    public static RTSPResponse handle(BufferedReader reader) {
        RTSPResponse rtspResponse = new RTSPResponse();
        List<String> items = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            log.trace(line);
            if (line.equals(eos)) {
                break;
            }
            items.add(line);
        }

        parseCode(rtspResponse, items.get(0));
        items.remove(0);

        parseHeaders(rtspResponse, items);

        return rtspResponse;
    }

    /**
     * Метод разбора ответа для получения статус кода.
     * <p>Первая полученная строка ответа разбирается для получения статус кода ответа.
     *
     * @param rtspResponse тело ответа
     * @param firstLine первая строка ответа
     */
    private static void parseCode(RTSPResponse rtspResponse, String firstLine) {
        rtspResponse.setStatusLine(firstLine);

        Matcher matcher = Pattern.compile("RTSP/1\\.0\\s(\\d{3})\\s(.*)").matcher(firstLine);
        if (matcher.find()) {
            rtspResponse.setCode(Integer.parseInt(matcher.group(1)));
            rtspResponse.setCodeDescription(matcher.group(2));
        } else {
            rtspResponse.setCode(418);
        }
    }

    /**
     * Метод разбора заголовков ответа.
     *
     * @param rtspResponse тело ответа
     * @param items строки, содержащие заголовки ответа
     */
    private static void parseHeaders(RTSPResponse rtspResponse, List<String> items) {
        for (String item : items) {
            String[] splitHeaders = item.split(": ");
            switch (splitHeaders[0]) {
                case cSeq:
                    rtspResponse.setCSeq(Integer.parseInt(splitHeaders[1]));
                    break;
                case wwwAuthenticate:
                    if (splitHeaders[1].contains("Digest"))
                        rtspResponse.setDigestAuth(splitHeaders[1]);
                    else
                        rtspResponse.setBasicAuth(splitHeaders[1]);
                    break;
                case session:
                    rtspResponse.setSession(splitHeaders[1].split(";")[0]);
                default:
                    break;
            }
        }
    }

}