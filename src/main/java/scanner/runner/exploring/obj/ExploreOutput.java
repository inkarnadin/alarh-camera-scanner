package scanner.runner.exploring.obj;

import lombok.Builder;
import lombok.Getter;
import scanner.runner.Target;

import java.util.Set;

@Getter
@Builder
public class ExploreOutput {

    private final Set<Target> targets;

}