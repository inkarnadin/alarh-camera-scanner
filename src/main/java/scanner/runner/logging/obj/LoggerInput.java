package scanner.runner.logging.obj;

import lombok.Builder;
import lombok.Getter;
import scanner.runner.Target;

import java.util.Set;

/**
 * Input logging object wrapper.
 *
 * @author inkarnadin
 * on 01-10-2022
 */
@Getter
@Builder
public class LoggerInput {

    private final Set<Target> targets;

}