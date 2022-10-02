package scanner.runner.logging.obj;

import lombok.Builder;
import lombok.Getter;
import scanner.runner.Target;

import java.util.Set;

@Getter
@Builder
public class LoggerInput {

    private final Set<Target> targets;

}