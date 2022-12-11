package scanner.runner;

import com.google.common.base.Stopwatch;

/**
 * Common abstract runner.
 *
 * @author inkarnadin
 */
@Deprecated
public abstract class AbstractRunner {

    protected final Stopwatch timer = Stopwatch.createStarted();

}
