package scanner.stat;

public enum EfficiencyItem {

    SCAN_EFFICIENCY { @Override public String toString() { return "Scanning efficiency"; } },
    BRUTE_EFFICIENCY { @Override public String toString() { return "Brute efficiency"; } },
    GET_FRAME_EFFICIENCY { @Override public String toString() { return "Getting frame efficiency"; } },

}