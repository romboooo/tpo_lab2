package org.example.core;

import java.io.IOException;
import java.io.Writer;

public abstract class MathFunction {
    protected static final double DEFAULT_EPSILON = 1e-6;
    protected static final int DEFAULT_MAX_ITERATIONS = 100;

    protected final double epsilon;
    protected final int maxIterations;

    protected MathFunction(){
        this(DEFAULT_EPSILON, DEFAULT_MAX_ITERATIONS);
    }
    protected MathFunction(double epsilon, int maxIterations) {
        this.epsilon = epsilon;
        this.maxIterations = maxIterations;
    }

    public abstract double solve(double x);
    public abstract boolean isDefined(double x);
    public abstract String getName();
    public abstract String getDomainDescription();

    public String formatForCsv(double x) {
        double result = solve(x);
        return String.format("%.6f,%s", x,
                Double.isNaN(result) ? "NaN" : String.format("%.10f", result));
    }

    public void exportToCsv(Writer writer, double start, double end, double step)
            throws IOException {
        writer.write("x," + getName() + "\n");
        for (double x = start; x <= end; x += step) {
            writer.write(formatForCsv(x) + "\n");
        }
        writer.flush();
    }

    protected static double normalizeAngle(double x) {
        double twoPi = 2.0 * Math.PI;
        x = x % twoPi;
        if (x > Math.PI) x -= twoPi;
        else if (x < -Math.PI) x += twoPi;
        return x;
    }

    protected static double clampToTrigRange(double value) {
        if (value > 1.0) return 1.0;
        if (value < -1.0) return -1.0;
        return value;
    }

    protected boolean isNearZero(double value) {
        return Math.abs(value) < epsilon;
    }
}
