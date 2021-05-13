package scanner.recover;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * List of keys for recovery items.
 *
 * @author inkarnadin
 */
@Getter
@RequiredArgsConstructor
public enum RecoveryElement {

    SOURCE_CHECKSUM("sourceChecksum"),
    STOP_SCAN_POINT("stopScanPoint"),
    SCANNING_STAT("scanningStat"),
    SCREENING_STAT("screeningStat");

    private final String description;

    /**
     * Find element by description.
     *
     * @param description definition of element
     * @return recovery element. If absent return {@code null}
     */
    public static RecoveryElement find(String description) {
        for (RecoveryElement position : RecoveryElement.values())
            if (position.description.equals(description))
                return position;
        return null;
    }

}
