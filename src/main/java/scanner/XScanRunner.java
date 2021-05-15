package scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.http.IpV4AddressRange;
import scanner.recover.RecoveryElement;
import scanner.recover.RecoveryManager;
import scanner.scan.CameraScanner;
import scanner.stat.ScanStatGatherer;
import scanner.stat.ScreenStatGatherer;

import java.util.ArrayList;
import java.util.List;

import static scanner.recover.RecoveryElement.*;

/**
 * Scanning ip range logic class.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class XScanRunner implements Runner {

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
            RecoveryManager.save(SCANNING_STAT, ScanStatGatherer.getStatsAsString());
            RecoveryManager.save(SCREENING_STAT, ScreenStatGatherer.getStatsAsString());

            result = new CameraScanner().scanning(range.list());

            int nbThreads =  Thread.getAllStackTraces().keySet().size();
            log.debug("active threads: " + nbThreads);
        } catch (Exception xep) {
            log.error("Error during check ip range: {}", xep.getMessage());
        }
        return result;
    }

}