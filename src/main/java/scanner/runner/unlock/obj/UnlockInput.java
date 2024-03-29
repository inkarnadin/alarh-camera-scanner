package scanner.runner.unlock.obj;

import lombok.Builder;
import lombok.Getter;
import scanner.runner.Target;

import java.util.Set;

/**
 * Input break object wrapper.
 *
 * @author inkarnadin
 * on 01-10-2022
 */
@Getter
@Builder
public class UnlockInput {

    private final Set<Target> addresses;
    private final Set<String> passwords;
    private final int repeatCount;

}