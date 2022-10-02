package scanner.runner;

import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.http.ip.IpV4AddressRange;
import scanner.http.ip.RangeUtils;
import scanner.runner.breaking.BreakerModule;
import scanner.runner.breaking.obj.BreakInput;
import scanner.runner.breaking.obj.BreakOutput;
import scanner.runner.exploring.ExplorerModule;
import scanner.runner.exploring.obj.ExploreInput;
import scanner.runner.exploring.obj.ExploreOutput;
import scanner.runner.logging.obj.LoggerInput;
import scanner.runner.logging.LoggerModule;

import java.util.Set;

@Slf4j
public class NewMainRunner {

    private final ExplorerModule explorerModule = new ExplorerModule();
    private final BreakerModule breakerModule = new BreakerModule();
    private final LoggerModule loggerModule = new LoggerModule();

    public void run() {
        Set<String> sources = Preferences.getRangesList();
        Set<String> passwords = Preferences.getPasswordsList();

        Set<IpV4AddressRange> ranges = RangeUtils.prepare(sources);
        for (IpV4AddressRange range : ranges) {
            ExploreOutput exploreOutput = explorerModule.execute(ExploreInput.builder()
                    .range(range)
                    .build());
            BreakOutput breakOutput = breakerModule.execute(BreakInput.builder()
                    .addresses(exploreOutput.getTargets())
                    .passwords(passwords)
                    .build());

            loggerModule.execute(LoggerInput.builder()
                    .targets(breakOutput.getTargets())
                    .build());
        }
    }



}