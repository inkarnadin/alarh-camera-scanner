package scanner.stat;

@Deprecated
public class StatDataHolder {

    public static final ScanStatGatherer SCAN_GATHERER = new ScanStatGatherer();
    public static final ScreenStatGatherer SCREEN_GATHERER = new ScreenStatGatherer();
    public static final EfficiencyGatherer EFFICIENCY_GATHERER = new EfficiencyGatherer();
    public static final TimeStatGatherer TIME_GATHERER = new TimeStatGatherer();

}