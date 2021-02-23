package scanner;

import scanner.brute.RTSPMode;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines and remembers the base path for credential validation.
 *
 * Sometimes the base path to the host does not allow checking whether it is possible to log in and returns a 404 code.
 * The Context class is needed to remember the choice of the verification path for each checked host.
 * Its value is determined on the first request with empty credentials. This avoids unnecessary checks on the wrong path.
 *
 * ORTHODOX mode will return by default.
 */
public class Context {

    private static final ConcurrentHashMap<String, RTSPMode> storage = new ConcurrentHashMap<>();

    public static void set(String ip, RTSPMode mode) {
        storage.put(ip, mode);
    }

    public static RTSPMode get(String ip) {
        return storage.getOrDefault(ip, RTSPMode.ORTHODOX);
    }

}