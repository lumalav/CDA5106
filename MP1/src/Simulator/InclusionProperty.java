package Simulator;

/**
 * Represents the two types of inclusivity supported by the cache
 */
public enum InclusionProperty {
    NonInclusive,
    Inclusive;

    public static InclusionProperty FromInteger(int x) {
        return x == 0 ? NonInclusive : Inclusive;
    }

    public static String ToString(InclusionProperty i) {
        return i == NonInclusive ? "non-inclusive" : "inclusive";
    }
}
