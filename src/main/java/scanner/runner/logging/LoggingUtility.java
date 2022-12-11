package scanner.runner.logging;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Logging utility class.
 *
 * @author inkarnadin
 * on 05-10-2022
 */
@Slf4j
public final class LoggingUtility {

    /**
     * Not instantiate constructor.
     */
    private LoggingUtility() {
        throw new AssertionError("Not instantiate utility class");
    }

    /**
     * Method for calculate and show total percent of completion subtask.
     *
     * @param numberOfItem number of current item
     * @param totalItemCount total items count
     */
    public static void showSubtaskPercentCompletion(int numberOfItem, int totalItemCount) {
        BigDecimal percent = BigDecimal.valueOf((double) numberOfItem / totalItemCount * 100).setScale(2, RoundingMode.FLOOR);
        log.info("subtask complete {}/{} ({}%)", numberOfItem, totalItemCount, percent);
    }

}