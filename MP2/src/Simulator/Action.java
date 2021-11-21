package Simulator;

/**
 * Represents a branch action
 */
public enum Action {
    Taken,
    NotTaken;

    public static Action FromString(String value) {
        value = value == null ? "" : value.trim().toLowerCase();

        byte[] bytes = value.getBytes();

        for (byte b : bytes) {
            if(b == 116) {
                return Taken;
            }
        }
        return NotTaken;
    }
}
