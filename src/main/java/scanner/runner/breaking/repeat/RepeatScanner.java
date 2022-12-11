package scanner.runner.breaking.repeat;

import lombok.extern.slf4j.Slf4j;
import scanner.runner.Target;
import scanner.runner.breaking.BreakType;
import scanner.runner.breaking.obj.BreakInput;
import scanner.runner.breaking.obj.BreakOutput;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс повторного сканирования диапазона.
 * <p> Все ранее найденные пароли повторно проверяются для еще не разблокированных адресов текущего диапазона.
 *
 * @author inkarnadin
 * on 01-10-2022
 */
@Slf4j
public class RepeatScanner {

    /**
     * Метод повторного сканирования адресов.
     * <p> На вход подаются выявленные в ходе рабочей сессии пароли, включая начально заданные. Новый список паролей
     * применяется повторно к еще не разблокированным адресам.
     *
     * @param input входной объект (служит для трансляции исходного списка паролей и адресов)
     * @param brokenTargets список разблокированных целей
     * @param action функция действия - рекурсивное обращение к модулю перебора
     * @return список разблокированных объектов
     */
    public Set<Target> scanning(BreakInput input, Set<Target> brokenTargets, Function<BreakInput, BreakOutput> action) {
        int repeatCount = input.getRepeatCount();
        if (repeatCount < 1) {
            log.trace("repeat count = {}, skipped", repeatCount);
            return Collections.emptySet();
        }

        Set<Target> result = new HashSet<>();
        Set<String> repeatPasswords = brokenTargets.stream()
                .filter(x -> x.getCredentials().isPresent())
                .map(p -> p.getCredentials().getPassword())
                .collect(Collectors.toSet());
        repeatPasswords.addAll(input.getPasswords());
        //repeatPasswords.removeAll(input.getPasswords());
        Set<Target> unbrokenTargets = input.getAddresses().stream()
                .filter(y -> !brokenTargets.contains(y))
                .collect(Collectors.toSet());

        if (repeatPasswords.isEmpty()) {
            log.trace("repeat password check list is empty, skipped");
            return Collections.emptySet();
        }

        log.debug("repeat password check list: {}", repeatPasswords);
        log.debug("unbroken targets list size: {}", unbrokenTargets.size());

        BreakOutput repeatResult = action.apply(BreakInput.builder()
                .addresses(unbrokenTargets)
                .passwords(repeatPasswords)
                .repeatCount(--repeatCount)
                .build()
        );
        for (Target repeatTarget : repeatResult.getTargets()) {
            if (repeatTarget.isBroken()) {
                if (repeatTarget.isFreeStream()) {
                    log.debug("repeat password check - empty password found");
                    repeatTarget.setBreakType(BreakType.REPEAT_EMPTY);
                    result.add(repeatTarget);
                } else {
                    log.debug("repeat password check - password found");
                    repeatTarget.setBreakType(BreakType.REPEAT);
                    result.add(repeatTarget);
                }
            }
        }
        return result;
    }

}