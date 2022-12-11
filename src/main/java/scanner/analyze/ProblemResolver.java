package scanner.analyze;

import lombok.extern.slf4j.Slf4j;
import scanner.ffmpeg.FFMpegExecutor;
import scanner.rtsp.RTSPPath;

import java.util.HashSet;
import java.util.Set;

/**
 * Rerun problem target according to the specified amendments.
 *
 * @author inkarnadin
 */
@Slf4j
@Deprecated
public class ProblemResolver {

    /**
     * Rerun problem target which was saved in {@link ProblemHolder} class.
     */
    @SuppressWarnings({"rawtypes"})
    public static void run() {
        try {
            Set<ProblemTarget<?>> localStore = new HashSet<>(ProblemHolder.STORE);
            for (ProblemTarget target : localStore) {
                Resolve value = target.getResolve();
                if (value instanceof PathResolve)
                    FFMpegExecutor.saveFrame(target.getCredentials(), target.getIp(), ((PathResolve) value).resolve());
                if (value instanceof AuthResolve)
                    FFMpegExecutor.saveFrame(((AuthResolve) value).resolve(), target.getIp(), RTSPPath.STANDARD);
            }
        } catch (Exception xep) {
            log.warn("Error during resolve problem addresses: {}", xep.getMessage());
        }
        ProblemHolder.clear();
    }

}