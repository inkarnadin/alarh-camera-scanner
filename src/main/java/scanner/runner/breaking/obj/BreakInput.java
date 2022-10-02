package scanner.runner.breaking.obj;

import lombok.Builder;
import lombok.Getter;
import scanner.runner.Target;

import java.util.Set;

@Getter
@Builder
public class BreakInput {

    private final Set<Target> addresses;
    private final Set<String> passwords;

}