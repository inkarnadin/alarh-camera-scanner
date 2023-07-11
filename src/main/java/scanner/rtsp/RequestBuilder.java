package scanner.rtsp;

import lombok.Getter;

import java.util.Objects;

/**
 * Класс для создания базовых RTSP-запросов.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
public class RequestBuilder {

    private final static String crcl = "\r\n";
    private final static String space = " ";
    private final static int port = 554;

    private final String ip;
    @Getter
    private final String credentials;

    /**
     * Конструктор для создания объекта.
     *
     * @param ip целевой адрес
     * @param credentials учетные данные в виде строки, разделенной двоеточием
     */
    public RequestBuilder(String ip, String credentials) {
        this.ip = ip;
        this.credentials = Objects.nonNull(credentials) ? credentials : "";
    }

    /**
     * Создание запроса для инициации метода <b>DESCRIBE<b/>.
     *
     * @return тело запроса
     */
    public String describe() {
        return RTSPContext.get(ip) == TransportMode.ORTHODOX
                ? new StringBuilder()
                    .append(RTSPMethod.DESCRIBE).append(space).append("rtsp://").append(credentials).append("@").append(ip).append(":").append(port)
                    .append("/").append(space).append("RTSP/1.0").append(crcl)
                    .append("CSeq:").append(space).append("1").append(crcl)
                    .append("Content-Type:").append(space).append("application/sdp").append(crcl)
                    .append(crcl)
                    .toString()
                : new StringBuilder()
                    .append(RTSPMethod.DESCRIBE).append(space).append("rtsp://").append(credentials).append("@").append(ip).append(":").append(port)
                    .append("/11").append(space).append("RTSP/1.0").append(crcl)
                    .append("CSeq:").append(space).append("1").append(crcl)
                    .append("Content-Type:").append(space).append("application/sdp").append(crcl)
                    .append(crcl)
                    .toString();
    }

}