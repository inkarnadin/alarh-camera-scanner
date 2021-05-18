package scanner;

import lombok.extern.slf4j.Slf4j;
import scanner.stat.EfficiencyGatherer;
import scanner.stat.ScanStatGatherer;
import scanner.stat.ScreenStatGatherer;
import scanner.stat.TimeStatGatherer;

/**
 * Create report logic class.
 *
 * @author inkarnadin
 */
@Slf4j
public class XReportRunner extends AbstractRunner {

    /**
     * Print report about checking.
     */
    public void run() {
        log.info(ScanStatGatherer.createReport());
        log.info(EfficiencyGatherer.createReport());
        log.info(TimeStatGatherer.createReport());
        log.info(ScreenStatGatherer.createReport());
    }

}