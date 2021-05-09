package scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.http.RangeManager;
import scanner.scan.CameraScanner;
import scanner.http.InetSocketAddressRange;
import scanner.stat.ScanStatGatherer;

import java.util.List;

import static scanner.stat.ScanStatEnum.ALL;
import static scanner.stat.ScanStatEnum.RANGES;

/**
 * Scan ip range logic class.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class XScanRunner implements Runner {

    private final List<String> listSources;

    /**
     * Execute scan ip range if set {@code -c} flag.
     *
     * The list of ranges passed as an argument is split into separate addresses,
     * which are checked for the availability of the port specified in the settings (by default, 554).
     *
     * The check state is saved before processing each new range.
     *
     * During the audit, statistical data are collected, which at the end are provided in the form of a report.
     */
    @Override
    public void run() {
        try {
            if (Preferences.check("-c")) {
                System.out.println("It can be very long. Please, wait...");
                System.out.println("See log files for more information: /logs/out.log");

                ScanStatGatherer.set(RANGES, listSources.size());
                for (String range : listSources)
                    RangeManager.prepare(range);

                ScanStatGatherer.set(ALL, RangeManager.count());
                log.info("addresses will be checked = " + RangeManager.count());

                final CameraScanner scanner = new CameraScanner();
                final List<InetSocketAddressRange> addressCache = RangeManager.getAddressCache();

                int c = 0;
                for (InetSocketAddressRange range : addressCache) {
                    RescueManager.save(range);
                    log.info("progress: ({}) {}/{}", range.toString(), ++c, addressCache.size());
                    scanner.scanning(range.list());
                }
            }
        } catch (Exception xep) {
            log.error("Error during check ip range: {}", xep.getMessage());
        }
    }

}