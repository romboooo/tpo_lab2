package org.example.core;

public abstract class TrigonometricFunction extends MathFunction {

    protected TrigonometricFunction(){
        super();
    }
    protected TrigonometricFunction(double epsilon, int maxIterations) {
        super(epsilon, maxIterations);
    }

    protected boolean isDiscontinuityPoint(double x, double period, double discontinuityOffset) {
        if (Double.isInfinite(x) || Double.isNaN(x)) {
            return true;
        }
        double normalized = ((x % period) + period) % period;
        return Math.abs(normalized - discontinuityOffset) < epsilon ||
                Math.abs(normalized - (discontinuityOffset + period)) < epsilon;
    }
}
