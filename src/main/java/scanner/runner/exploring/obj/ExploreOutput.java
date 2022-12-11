package scanner.runner.exploring.obj;

import lombok.Builder;
import lombok.Getter;
import scanner.runner.Target;

import java.util.Set;

/**
 * Output explore object wrapper.
 *
 * @author inkarnadin
 * on 01-10-2022
 */
@Getter
@Builder
public class ExploreOutput {

    private final Set<Target> targets;

}