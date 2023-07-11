package scanner.recover;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление списка ключей, необходимых для восстановления рабочей сессии в случае ее прерывания.
 *
 * @author inkarnadin
 * on 11-05-2021
 */
@Getter
@RequiredArgsConstructor
public enum RecoveryElement {

    SOURCE_CHECKSUM("sourceChecksum"),
    STOP_SCAN_RANGE("stopScanRange"),
    SCANNING_STAT("scanningStat"),
    SCREENING_STAT("screeningStat");

    private final String description;

    /**
     * Поиск ключа восстановления по его описанию.
     *
     * @param description описание элемента
     * @return элемент, который необходимо восстановить, если ничего не найдено, вернет {@code null}
     */
    public static RecoveryElement find(String description) {
        for (RecoveryElement position : RecoveryElement.values()) {
            if (position.description.equals(description)) {
                return position;
            }
        }
        return null;
    }

}
