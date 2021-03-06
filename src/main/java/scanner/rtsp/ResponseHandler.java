package scanner.rtsp;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Socket response handler.
 *
 * @author inkarnadin
 */
@Slf4j
public class ResponseHandler {

    private static String eos = "";

    private static final String cSeq = "CSeq";
    private static final String wwwAuthenticate = "WWW-Authenticate";
    private static final String session = "Session";

    /**
     * Handle socket answer.
     *
     * @param reader socket input stream.
     * @return parsed response.
     */
    @SneakyThrows
    public static RTSPStateStore handle(BufferedReader reader) {
        RTSPStateStore store = new RTSPStateStore();
        List<String> items = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            log.trace(line);
            if (line.equals(eos)) {
                break;
            }
            items.add(line);
        }

        parseCode(store, items.get(0));
        items.remove(0);

        parseHeaders(store, items);

        return store;
    }

    private static void parseCode(RTSPStateStore store, String firstLine) {
        store.setStatusLine(firstLine);

        Matcher matcher = Pattern.compile("RTSP/1\\.0\\s(\\d{3})\\s(.*)").matcher(firstLine);
        if (matcher.find()) {
            store.setCode(Integer.parseInt(matcher.group(1)));
            store.setCodeDescription(matcher.group(2));
        } else {
            store.setCode(418);
        }
    }

    private static void parseHeaders(RTSPStateStore store, List<String> items) {
        for (String item : items) {
            String[] splitHeaders = item.split(": ");
            switch (splitHeaders[0]) {
                case cSeq:
                    store.setCSeq(Integer.parseInt(splitHeaders[1]));
                    break;
                case wwwAuthenticate:
                    if (splitHeaders[1].contains("Digest"))
                        store.setDigestAuth(splitHeaders[1]);
                    else
                        store.setBasicAuth(splitHeaders[1]);
                    break;
                case session:
                    store.setSession(splitHeaders[1].split(";")[0]);
                default:
                    break;
            }
        }
    }

}