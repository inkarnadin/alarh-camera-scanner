package scanner.runner.logging;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Вспомогательный класс для логирования данных.
 *
 * @author inkarnadin
 * on 05-10-2022
 */
@Slf4j
@UtilityClass
public final class LoggingUtility {

    /**
     * Метод расчета и вывода количества завершенных и оставшихся подзадач.
     *
     * @param numberOfItem количество элементов
     * @param totalItemCount общее количество элементов
     */
    public static void printSubtaskPercentCompletion(int numberOfItem, int totalItemCount) {
        BigDecimal percent = BigDecimal.valueOf((double) numberOfItem / totalItemCount * 100).setScale(2, RoundingMode.FLOOR);
        log.info("subtask complete {}/{} ({}%)", numberOfItem, totalItemCount, percent);
    }

}