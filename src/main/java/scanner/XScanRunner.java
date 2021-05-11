package scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.recover.RecoveryManager;
import scanner.recover.RecoveryElement;
import scanner.scan.CameraScanner;
import scanner.http.InetSocketAddressRange;

import java.util.ArrayList;
import java.util.List;

/**
 * Scanning ip range logic class.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class XScanRunner implements Runner {

    private final InetSocketAddressRange range;

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
            RecoveryManager.save(RecoveryElement.STOP_SCAN_POINT, range.first().getHostName());
            result = new CameraScanner().scanning(range.list());
        } catch (Exception xep) {
            log.error("Error during check ip range: {}", xep.getMessage());
        }
        return result;
    }

}