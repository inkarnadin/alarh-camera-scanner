package scanner.runner.breaking;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import scanner.runner.Target;
import scanner.runner.breaking.cve.CVEScanner;
import scanner.runner.breaking.obj.BreakInput;
import scanner.runner.breaking.obj.BreakOutput;
import scanner.runner.breaking.repeat.RepeatScanner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class BreakerModule {

    private final CVEScanner cveScanner = new CVEScanner();
    private final BruteScanner bruteScanner = new BruteScanner();
    private final RepeatScanner repeatScanner = new RepeatScanner();

    public BreakOutput execute(@NotNull BreakInput input) {
        Set<Target> result = new HashSet<>();
        try {
            Set<Target> targets = input.getAddresses();
            Set<String> passwords = input.getPasswords();

            int i = 0;
            for (Target target : targets) {
                String host = target.getHost();

                // cve
                Target resultItem = cveScanner.scanning(host);
                if (resultItem.isBroken()) {
                    result.add(resultItem);
                    showPercent(++i, targets.size());
                    continue;
                }

                // brute
                resultItem = bruteScanner.scanning(host, passwords.toArray(new String[0]));
                result.add(resultItem);
                showPercent(++i, targets.size());
            }

            //repeat
            Set<Target> repeatResult = repeatScanner.scanning(input, result, this::execute);
            result.addAll(repeatResult);
        } catch (Exception xep) {
            log.error("error during brute ip range [{}]: {}", input.getAddresses(), xep.getMessage());
        }
        return BreakOutput.builder()
                .targets(result)
                .build();
    }

    private void showPercent(int i, int size) {
        BigDecimal percent = BigDecimal.valueOf((double) i / size * 100).setScale(2, RoundingMode.FLOOR);
        log.info("subtask complete {}/{} ({}%)", i, size, percent);
    }



}