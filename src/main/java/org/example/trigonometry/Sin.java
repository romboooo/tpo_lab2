package org.example.trigonometry;

import org.example.core.TrigonometricFunction;

public class Sin extends TrigonometricFunction {

    public Sin() {
        super();
    }

    public Sin(double epsilon, int maxIterations) {
        super(epsilon, maxIterations);
    }

    @Override
    public String getName() {
        return "sin";
    }

    @Override
    public String getDomainDescription() {
        return "x ∈ ℝ (все действительные числа)";
    }

    @Override
    public boolean isDefined(double x) {
        return !Double.isInfinite(x) && !Double.isNaN(x);
    }

    @Override
    public double solve(double x) {
        if (!isDefined(x)) {
            return Double.NaN;
        }

        x = normalizeAngle(x);

        double result = 0.0;
        double term = x;
        int n = 1;

        while (Math.abs(term) > epsilon) {
            if (n >= maxIterations) {
                break;
            }
            result += term;
            term *= -x * x / ((2.0 * n) * (2.0 * n + 1));
            n++;
        }

        return clampToTrigRange(result);
    }
}