package scanner.runner.logging;

import lombok.extern.slf4j.Slf4j;
import scanner.runner.Target;
import scanner.runner.logging.obj.LoggerInput;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Slf4j
public class LoggerModule {

    public void execute(LoggerInput input) {
        Set<Target> targets = input.getTargets();
        targets.stream()
                .filter(Target::isBroken)
                .forEach(t -> log.info(t.asFullFormattedString()));
    }

}