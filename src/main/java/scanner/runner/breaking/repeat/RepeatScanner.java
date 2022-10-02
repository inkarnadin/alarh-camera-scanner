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

@Slf4j
public class RepeatScanner {

    public Set<Target> scanning(BreakInput input, Set<Target> brokenTargets, Function<BreakInput, BreakOutput> action) {
        Set<Target> result = new HashSet<>();
        Set<String> repeatPasswords = brokenTargets.stream()
                .filter(x -> x.getCredentials().isPresent())
                .map(p -> p.getCredentials().getPassword())
                .collect(Collectors.toSet());
        repeatPasswords.removeAll(input.getPasswords());
        log.info("repeat password check list: {}", repeatPasswords);

        Set<Target> unbrokenTargets = input.getAddresses().stream()
                .filter(x -> !x.isBroken())
                .collect(Collectors.toSet());
        log.info("unbroken targets list size: {}", unbrokenTargets.size());

        if (repeatPasswords.isEmpty()) {
            log.debug("repeat password check list is empty, skipped");
            return Collections.emptySet();
        }
        BreakOutput repeatResult = action.apply(BreakInput.builder()
                .addresses(unbrokenTargets)
                .passwords(repeatPasswords)
                .build());
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