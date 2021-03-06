package scanner;

import scanner.brute.TransportMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines and remembers the base path for credential validation.
 *
 * Sometimes the base path to the host does not allow checking whether it is possible to log in and returns a 404 code.
 * The Context class is needed to remember the choice of the verification path for each checked host.
 * Its value is determined on the first request with empty credentials. This avoids unnecessary checks on the wrong path.
 *
 * ORTHODOX mode will return by default.
 *
 * @author inkarnadin
 */
public class Context {

    private static final Map<String, TransportMode> storage = new HashMap<>();

    /**
     * Set certain for given IP address. For one IP address must be only one active mode.
     * It override previous value.
     *
     * @param ip target IP address.
     * @param mode current active mode.
     */
    public static void set(String ip, TransportMode mode) {
        storage.put(ip, mode);
    }

    /**
     * Getting current RTSP mode for certain IP.
     *
     * @param ip certain IP address.
     * @return active RTSP mode.
     */
    public static TransportMode get(String ip) {
        return storage.getOrDefault(ip, TransportMode.ORTHODOX);
    }

}