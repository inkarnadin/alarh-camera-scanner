package scanner.rtsp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Класс тела ответа, полученного на запрос по протоколу RTSP.
 * <p>Содержит основные заголовки первичного ответа (метод <b>DESCRIBE<b/>).
 *
 * @author inkarnadin
 * on 01-01-2022
 */
@Data
@EqualsAndHashCode
public class RTSPResponse {

    private String statusLine;

    private int code;
    private String codeDescription;

    private int cSeq;
    private String dateAsString;

    private String digestAuth;
    private String basicAuth;
    private String session;

}