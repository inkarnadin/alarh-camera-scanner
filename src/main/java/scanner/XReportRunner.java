package scanner;

import lombok.extern.slf4j.Slf4j;
import scanner.stat.ScanStatGatherer;
import scanner.stat.ScreenStatGatherer;

/**
 * Create report logic class.
 *
 * @author inkarnadin
 */
@Slf4j
public class XReportRunner implements Runner {

    /**
     * Print report about checking.
     */
    @Override
    public void run() {
        log.info(ScanStatGatherer.createReport());
        log.info(ScreenStatGatherer.createReport());
    }

}