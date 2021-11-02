package Simulator;

/**
 * Depicts the different type of replacement policies supported by the simulator
 */
public enum ReplacementPolicyType {
    LRU,
    PLRU,
    OPT;

    public static ReplacementPolicyType FromInteger(int x) {
        switch (x) {
            case 0:
                return LRU;
            case 1:
                return PLRU;
            default:
                return OPT;
        }
    }

    public static String ToString(ReplacementPolicyType i) {
        switch (i) {
            case LRU:
                return "LRU";
            case PLRU:
                return "Pseudo-LRU";
            default:
                return "Optimal";
        }
    }
}