package scanner.runner;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.http.ip.IpV4AddressRange;
import scanner.recover.RecoveryManager;
import scanner.runner.exploring.CameraScanner;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
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
@NoArgsConstructor
public class ScanRunner extends AbstractRunner {

    /**
     * Method execute scanning ip range.
     * <p> The check state is saved before processing each new range.
     * <p> During the audit, statistical data are collected, which at the end are provided in the form of a report.
     *
     * @return list of targets
     */
    public Set<String> run(IpV4AddressRange range) {
        if (!timer.isRunning()) {
            timer.start();
        }

        Set<String> result = new HashSet<>();
        try {
            Set<InetSocketAddress> addresses = range.getAddresses();

            RecoveryManager.save(STOP_SCAN_RANGE, range.getSourceRange());
            RecoveryManager.save(SCANNING_STAT, SCAN_GATHERER.getStatsAsString());
            RecoveryManager.save(SCREENING_STAT, SCREEN_GATHERER.getStatsAsString());

            result = new CameraScanner().scanning(addresses);

            int nbThreads =  Thread.getAllStackTraces().keySet().size();
            log.debug("active threads: " + nbThreads);
        } catch (Exception xep) {
            log.error("error during check ip range: {}", xep.getMessage());
        }

        TIME_GATHERER.set(TOTAL_SCAN_TIME, timer.elapsed(TimeUnit.MILLISECONDS));
        timer.stop();

        return result;
    }

}