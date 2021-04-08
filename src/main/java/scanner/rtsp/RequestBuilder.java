package scanner.rtsp;

import scanner.Context;
import scanner.brute.TransportMode;

import java.util.Objects;

/**
 * Basic RTSP request builder.
 *
 * @author inkarnadin
 */
public class RequestBuilder {

    private final static String crcl = "\r\n";
    private final static String space = " ";
    private final static int port = 554;

    private final String ip;
    private final String credentials;

    public RequestBuilder(String ip, String credentials) {
        this.ip = ip;
        this.credentials = Objects.nonNull(credentials) ? credentials + "@" : "";
    }

    /**
     * Create request for a method DESCRIBE.
     *
     * @return request body.
     */
    public String describe() {
        return Context.get(ip) == TransportMode.ORTHODOX
                ? new StringBuilder()
                    .append(RTSPMethod.DESCRIBE).append(space).append("rtsp://").append(credentials).append(ip).append(":").append(port)
                    .append("/").append(space).append("RTSP/1.0").append(crcl)
                    .append("CSeq:").append(space).append("1").append(crcl)
                    .append("Content-Type:").append(space).append("application/sdp").append(crcl)
                    .append(crcl)
                    .toString()
                : new StringBuilder()
                    .append(RTSPMethod.DESCRIBE).append(space).append("rtsp://").append(credentials).append(ip).append(":").append(port)
                    .append("/11").append(space).append("RTSP/1.0").append(crcl)
                    .append("CSeq:").append(space).append("1").append(crcl)
                    .append("Content-Type:").append(space).append("application/sdp").append(crcl)
                    .append(crcl)
                    .toString();
    }

    /**
     * Create request for a method SETUP.
     *
     * @return request body.
     */
    public String setup() {
        return new StringBuilder()
                .append(RTSPMethod.SETUP).append(space).append("rtsp://").append(credentials).append(ip).append(":").append(port)
                .append("/Streaming/Channels/101/trackID=1").append(space).append("RTSP/1.0").append(crcl)
                .append("CSeq:").append(space).append("2").append(crcl)
                .append("Transport:").append(space).append("RTP/AVP/TCP;unicast;interleaved=0-1").append(crcl)
                .append("User-Agent:").append(space).append("Lavf58.20.100").append(crcl)
                .append(crcl)
                .toString();
    }

    /**
     * Create request for a method PLAY.
     *
     * @param session active socket session
     * @return request body.
     */
    public String play(String session) {
        return new StringBuilder()
                .append(RTSPMethod.PLAY).append(space).append("rtsp://").append(credentials).append(ip).append(":").append(port)
                .append("/Streaming/Channels/101").append(space).append("RTSP/1.0").append(crcl)
                .append("CSeq:").append(space).append("3").append(crcl)
                .append("Range:").append(space).append("ntp=0.000-0.001").append(crcl)
                .append("User-Agent:").append(space).append("Lavf58.20.100").append(crcl)
                .append("Session: ").append(space).append(session).append(crcl)
                .append("Authorization: ").append(space).append(" ").append(crcl)
                .append(crcl)
                .toString();
    }

}