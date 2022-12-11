package scanner.rtsp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление базовых путей, задействованных для протокола RTSP.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
@RequiredArgsConstructor
public enum RTSPPath {

    STANDARD("/11"),
    BASE("");

    @Getter
    private final String value;

}