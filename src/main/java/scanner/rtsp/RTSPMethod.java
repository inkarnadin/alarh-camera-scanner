package scanner.rtsp;

/**
 * Перечисление допустимых RTSP-методов.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
public enum RTSPMethod {

    OPTIONS,
    GET_PARAMETER,
    SET_PARAMETER,
    DESCRIBE,
    PLAY,
    PLAY_NOTIFY,
    REDIRECT,
    PAUSE,
    TEARDOWN,
    SETUP

}