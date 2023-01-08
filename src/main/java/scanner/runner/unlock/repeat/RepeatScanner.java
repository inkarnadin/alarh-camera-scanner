package scanner.runner.unlock.repeat;

import lombok.extern.slf4j.Slf4j;
import scanner.runner.Target;
import scanner.runner.unlock.UnlockType;
import scanner.runner.unlock.obj.UnlockInput;
import scanner.runner.unlock.obj.UnlockOutput;

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
     * @param input входной объект (служит для трансляции исходной информации)
     * @param targets список целей, проведенных через другие проверки хотя бы один раз
     * @param action функция действия - рекурсивное обращение к модулю перебора
     * @return список разблокированных объектов
     */
    public Set<Target> scanning(UnlockInput input, Set<Target> targets, Function<UnlockInput, UnlockOutput> action) {
        int repeatCount = input.getRepeatCount();
        if (repeatCount < 1) {
            log.trace("repeat count = {}, skipped", repeatCount);
            return Collections.emptySet();
        }

        Set<Target> result = new HashSet<>();
        Set<String> repeatPasswords = targets.stream()
                .filter(x -> x.getCredentials().isPresent())
                .map(p -> p.getCredentials().getPassword())
                .collect(Collectors.toSet());

        if (!repeatPasswords.isEmpty()) {
            log.debug("new passwords repeat check list: {}", repeatPasswords);
        }
        repeatPasswords.addAll(input.getPasswords());

        Set<Target> unbrokenTargets = targets.stream()
                .filter(f -> !f.isBroken())
                .collect(Collectors.toSet());
        log.debug("unbroken targets list size: {}", unbrokenTargets.size());

        if (repeatPasswords.isEmpty()) {
            log.trace("repeat password check list is empty, skipped");
            return Collections.emptySet();
        }

        UnlockOutput repeatResult = action.apply(UnlockInput.builder()
                .addresses(unbrokenTargets)
                .passwords(repeatPasswords)
                .repeatCount(--repeatCount)
                .build()
        );
        for (Target repeatTarget : repeatResult.getTargets()) {
            if (repeatTarget.isBroken()) {
                if (repeatTarget.isFreeStream()) {
                    log.debug("repeat password check - empty password found");
                    repeatTarget.setUnlockType(UnlockType.REPEAT_EMPTY);
                    result.add(repeatTarget);
                } else {
                    log.debug("repeat password check - password found");
                    repeatTarget.setUnlockType(UnlockType.REPEAT);
                    result.add(repeatTarget);
                }
            }
        }
        return result;
    }

}