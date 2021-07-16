package components;

/**
 * A collection of pure math functions
 */
public class MathUtils {

    /**
     * Signum, but 0 is reported as being positive (1).
     * 
     * @param val Value to signum
     * @return -1 if negative, 1 if positive or zero.
     */
    public static double nonZeroSignum(double val) {
        if (val >= 0) {
            return 1;
        }
        return -1;
    }

    public static int clamp(int value, int lower, int upper) {
        return Math.max(Math.min(value, upper), lower);
    }

    public static double clamp(double value, double lower, double upper) {
        return Math.max(Math.min(value, upper), lower);
    }

}