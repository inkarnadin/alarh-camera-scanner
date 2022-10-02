package scanner.runner.breaking.obj;

import lombok.Builder;
import lombok.Getter;
import scanner.runner.Target;

import java.util.Set;

@Getter
@Builder
public class BreakOutput {

    private final Set<Target> targets;

}