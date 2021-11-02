package Simulator;

/**
 * Represents the type of operation coming from the trace file
 */
public enum ProcessorRequest {
    Read,
    Write;
    
    public static ProcessorRequest FromString(String value) {
        value = value == null ? "" : value.trim().toLowerCase();

        byte[] bytes = value.getBytes();

        for (byte b : bytes) {
            if(b == 114) {
                return Read;
            }
        }
        return Write;
    }
}
