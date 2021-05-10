package scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.brute.basic.BasicAuthScanner;

import java.util.List;

/**
 * Find password basic auth logic class.
 * It will be part of brute logic class.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class XBasicAuthBruteRunner implements Runner {

    private final List<String> listSources;
    private final List<String> listPasswords;

    /**
     * Execute find basic auth password if set {@code -ba} flag.
     */
    public void run() {
        try {
            if (Preferences.check("-ba")) {
                final BasicAuthScanner basicAuthScanner = new BasicAuthScanner();
                int i = 0;
                for (String ip : listSources) {
                    log.info("progress: {} {}/{}", ip, ++i, listSources.size());
                    basicAuthScanner.brute(ip, listPasswords.toArray(new String[0]));
                }
            }
        } catch (Exception xep) {
            log.error("Error during basic auth brute: {}", xep.getMessage());
        }
    }

}