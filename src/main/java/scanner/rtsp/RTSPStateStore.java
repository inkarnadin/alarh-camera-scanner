package scanner.rtsp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RTSPStateStore {

    private String statusLine;

    private int code;
    private String codeDescription;

    private int cSeq;
    private String dateAsString;

    private String digestAuth;
    private String basicAuth;
    private String session;

}