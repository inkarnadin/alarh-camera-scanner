package scanner.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.http.IpV4AddressRange;
import scanner.recover.RecoveryManager;
import scanner.scan.CameraScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static scanner.recover.RecoveryElement.*;
import static scanner.stat.StatDataHolder.*;
import static scanner.stat.TimeStatItem.TOTAL_SCAN_TIME;

/**
 * Scanning ip range logic class.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class XScanRunner extends AbstractRunner {

    private final IpV4AddressRange range;

    /**
     * Execute scanning ip range.
     *
     * <p> The check state is saved before processing each new range.
     *
     * <p> During the audit, statistical data are collected, which at the end are provided in the form of a report.
     *
     * @return list of targets
     */
    public List<String> run() {
        List<String> result = new ArrayList<>();
        try {
            RecoveryManager.save(STOP_SCAN_POINT, range.first().toString());
            RecoveryManager.save(SCANNING_STAT, SCAN_GATHERER.getStatsAsString());
            RecoveryManager.save(SCREENING_STAT, SCREEN_GATHERER.getStatsAsString());

            result = new CameraScanner().scanning(range.list());

            int nbThreads =  Thread.getAllStackTraces().keySet().size();
            log.debug("active threads: " + nbThreads);
        } catch (Exception xep) {
            log.error("Error during check ip range: {}", xep.getMessage());
        }

        TIME_GATHERER.set(TOTAL_SCAN_TIME, timer.elapsed(TimeUnit.MILLISECONDS));
        timer.stop();

        return result;
    }

}