package scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.scan.CameraScanRangeManager;
import scanner.scan.CameraScanner;

import java.net.InetSocketAddress;
import java.util.List;

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
     */
    @Override
    public void run() {
        try {
            if (Preferences.check("-c")) {
                System.out.println("It can be very long. Please, wait...");
                System.out.println("See log files for more information: /logs/out.log");

                for (String range : listSources)
                    CameraScanRangeManager.prepareSinglePortScanning(range);
                log.info("addresses will be checked = " + CameraScanRangeManager.count());

                final CameraScanner scanner = new CameraScanner();
                final List<List<InetSocketAddress>> addressCache = CameraScanRangeManager.getAddressCache();

                int c = 0;
                for (List<InetSocketAddress> listOfIpAddresses : addressCache) {
                    log.info("progress: ({}) {}/{}", listOfIpAddresses.get(0), ++c, addressCache.size());
                    scanner.scanning(listOfIpAddresses);
                }
            }
        } catch (Exception xep) {
            log.error("Error during check ip range: {}", xep.getMessage());
        }
    }

}