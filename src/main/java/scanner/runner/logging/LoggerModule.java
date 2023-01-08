package scanner.runner.logging;

import lombok.extern.slf4j.Slf4j;
import scanner.runner.Target;
import scanner.runner.logging.obj.LoggerInput;

import java.util.Set;

/**
 * Модуль логирования результата анализа.
 *
 * @author inkarnadin
 * on 08-01-2023
 */
@Slf4j
public class LoggerModule {

    /**
     * Метод выполнения записи результата в лог.
     *
     * @param input входной объект-обертка для логирования результата
     */
    public void execute(LoggerInput input) {
        Set<Target> targets = input.getTargets();
        targets.stream()
                .filter(Target::isBroken)
                .forEach(t -> log.info(t.asFullFormattedString()));
    }

}