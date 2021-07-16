package components;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class PID {
    private String filename;

    private double kp;
    private double ki;
    private double kd;

    private double p = 0;
    private double i = 0;
    private double d = 0;

    public double target;

    public double error = 0;
    public NDouble pError = new NDouble();

    public double time = 0;
    public NDouble pTime = new NDouble();

    public PID(String filename) {
        this.filename = filename;
    }

    public PID(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public double update(double input) {
        time = System.currentTimeMillis();
        error = target - input;

        double timeDelta = 0;
        if (!pTime.nil) {
            timeDelta = time - pTime.val;
        }

        double errorDelta = 0;
        if (!pError.nil) {
            errorDelta = error - pError.val;
        }

        p = kp * error;
        // Kp(e)

        i = i + (ki * error);
        // Ki ∫e(Δt)

        d = kd * (errorDelta / (timeDelta / 1000));
        // Kd(Δe/Δt)

        pTime.val = time;
        pError.val = error;

        // Stupid way to check for NaN
        // NaN != NaN, but a number == a number
        // so (var != var) -> (val == NaN)
        if (p != p) {
            p = 0;
        }
        if (i != i) {
            i = 0;
        }
        if (d != d) {
            d = 0;
        }

        return p + i + d;
    }

    public void setFromArry(double[] arr) {
        kp = arr[0];
        ki = arr[1];
        kd = arr[2];
    }

    public double[] readPIDVals() {
        double[] r = new double[3];
        try {
            String contents = "";
            File fObj = new File(filename);
            Scanner reader = new Scanner(fObj);
            while (reader.hasNextLine()) {
                contents += reader.nextLine();
            }
            reader.close();
            String[] split = contents.split(",");
            for (int i = 0; i < r.length; i++) {
                r[i] = Double.parseDouble(split[i]);
            }
        } catch (FileNotFoundException e) {
            System.out.println(String.format("File: %s cannot be found!", filename));
        }
        return r;
    }
}
