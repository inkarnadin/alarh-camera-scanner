package scanner.analyze;

import scanner.rtsp.RTSPPath;

/**
 * Stream path problem solution.
 *
 * @author inkarnadin
 */
@Deprecated
public class PathResolve implements Resolve<RTSPPath> {

    /**
     * Get solution.
     *
     * @return base stream path for trying checking
     */
    @Override
    public RTSPPath resolve() {
        return RTSPPath.BASE;
    }

}