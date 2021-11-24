package Simulator;

/**
 * Represents the types of prediction policies supported by the simulator
 */
public enum PredictionPolicyType {
    Smith,Bimodal,Gshare,Hybrid;

    public static PredictionPolicyType FromString(String value) {
        value = value == null ? "" : value.trim().toLowerCase();

        switch(value) {
            case "hybrid":
                return Hybrid;
            case "smith":
                return Smith;
            case "bimodal":
                return Bimodal;
            default:
                return Gshare;
        }
    }
}
