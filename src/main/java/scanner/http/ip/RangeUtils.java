package scanner.http.ip;

import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.recover.RecoveryManager;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static scanner.Preferences.ALLOW_RECOVERY_SCANNING;
import static scanner.recover.RecoveryElement.STOP_SCAN_RANGE;

/**
 * Вспомогательный класс разбора диапазонов.
 *
 * @author inkarnadin
 * on 11-05-2021
 */
@Slf4j
public final class RangeUtils {

    /**
     * Не создаваемый конструктор вспомогательного класса.
     */
    private RangeUtils() {
        throw new AssertionError("Utility class can't be instantiate");
    }

    /**
     * Метод подготовки диапазона адресов.
     *
     * @param listSources список диапазонов адресов в виде строк
     * @return список диапазонов в виде объектов
     */
    public static Set<IpV4AddressRange> prepare(Set<String> listSources) {
        log.info("count of ranges: {}", listSources.size());
        Set<IpV4AddressRange> result = new TreeSet<>();
        for (String rangeAsString : listSources) {
            log.trace("prepare range: {}", rangeAsString);
            IpV4AddressRange ipV4AddressRange = new IpV4AddressRange(rangeAsString);

            if (result.isEmpty() && needRestore(ipV4AddressRange)) {
                log.debug("need restore flag is active, prepare this range was skipped");
                continue;
            }
            result.add(ipV4AddressRange);
        }

        log.info("total addresses for scanning: {}", RangeUtils.count(result));
        return result;
    }

    /**
     * Метод получения количества адресов во всех диапазонах.
     *
     * @param ranges список диапазонов
     * @return количество уникальных целевых адресов
     */
    public static long count(Set<IpV4AddressRange> ranges) {
        return ranges.stream()
                .mapToLong(IpV4AddressRange::getCount)
                .sum();
    }

    /**
     * Метод проверки необходимости восстановления предыдущей рабочей сессии на текущем диапазоне.
     * <p>Если флаг сканирования установлен в состояние {@code true}, то производится проверка, что
     * сессия закончилась на текущем переданном диапазоне.
     *
     * @param range проверяемый диапазон
     * @return состояние восстановления, если {@code true} - восстановление рабочей сессии должно произойти с текущего
     * диапазона, иначе нет.
     */
    private static boolean needRestore(IpV4AddressRange range) {
        if (Preferences.parseBoolean(ALLOW_RECOVERY_SCANNING)) {
            String stopScanAddress = RecoveryManager.getRestoredValue(STOP_SCAN_RANGE);
            if (Objects.nonNull(stopScanAddress)) {
                return !Objects.equals(range.getSourceRange(), stopScanAddress);
            }
        }
        return false;
    }

}