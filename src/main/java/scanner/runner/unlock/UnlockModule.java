package scanner.runner.unlock;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import scanner.runner.Target;
import scanner.runner.unlock.brute.BruteScanner;
import scanner.runner.unlock.cve.CVEScanner;
import scanner.runner.unlock.obj.UnlockInput;
import scanner.runner.unlock.obj.UnlockOutput;
import scanner.runner.unlock.repeat.RepeatScanner;

import java.util.HashSet;
import java.util.Set;

import static scanner.runner.logging.LoggingUtility.printSubtaskPercentCompletion;

/**
 * Модуль разблокировки доступа к устройству.
 *
 * @author inkarnadin
 * on 02-10-2022
 */
@Slf4j
public class UnlockModule {

    private final CVEScanner cveScanner = new CVEScanner();
    private final BruteScanner bruteScanner = new BruteScanner();
    private final RepeatScanner repeatScanner = new RepeatScanner();

    /**
     * Метод выполнения разблокировки целей с помощью перебора.
     *
     * @param input входной объект-обертка
     * @return выходной объект-обертка
     */
    public UnlockOutput execute(@NotNull UnlockInput input) {
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
                    printSubtaskPercentCompletion(++i, targets.size());
                    continue;
                }

                // brute
                resultItem = bruteScanner.scanning(host, passwords.toArray(new String[0]));
                result.add(resultItem);
                printSubtaskPercentCompletion(++i, targets.size());
            }

            //repeat
            Set<Target> repeatResult = repeatScanner.scanning(input, result, this::execute);
            result.addAll(repeatResult);
        } catch (Exception xep) {
            log.error("error during brute ip range [{}]: {}", input.getAddresses(), xep.getMessage());
        }
        return UnlockOutput.builder()
                .targets(result)
                .build();
    }

}