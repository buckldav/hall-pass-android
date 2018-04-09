package buckley.hallpass.model;

/**
 * @author David Buckley
 * Updated by David 4/3/2018
 *
 * ClassPeriod enums for use as keys.
 */

public enum ClassPeriod {
    A1, A2, A3, A4, B1, B2, B3, B4;

    public static ClassPeriod getFromInt(int number) {
        return ClassPeriod.values()[number];
    }
}
