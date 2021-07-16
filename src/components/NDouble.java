package components;

/**
 * The point of this class is to allow null values to be handled wiht respect to
 * error.
 */
public class NDouble {
    public double val;
    public boolean nil = true;

    public NDouble() {
    }

    public NDouble(int n) {
        val = n;
        nil = false;
    }

    public void set(int n) {
        val = n;
        nil = false;
    }

    public double get() {
        return val;
    }

    public boolean isNil() {
        return nil;
    }
}
