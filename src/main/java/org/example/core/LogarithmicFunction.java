package org.example.core;

public abstract class LogarithmicFunction extends MathFunction {

    protected LogarithmicFunction(){
        super();
    }

    protected LogarithmicFunction(double epsilon, int maxIterations){
        super(epsilon, maxIterations);
    }

    @Override
    public boolean isDefined(double x){
        return x > 0 && !Double.isInfinite(x);
    }

    protected static double[] normalizeArgument(double x){
        int k = 0;
        double normalized = x;

        while (normalized > 2.0){
            normalized /= 2.0;
            k++;
        }
        while (normalized < 0.5){
            normalized *= 2.0;
            k--;
        }

        return new double[]{normalized, k};
    }

    protected double computeLnNormalized(double x){
        // ряд: ln((1+y)/(1-y)) = 2 * (y + y³/3 + y⁵/5 + ...)
        // где y = (x-1)/(x+1), сходится для x ∈ (0, ∞)
        double y = (x - 1.0) / (x + 1.0);

        double result = 0.0;
        double term = y;
        int n = 1;

        while (Math.abs(term / n) > epsilon) {
            if (n >= maxIterations * 2) {
                break;
            }
            result += term / n;
            term *= y * y;
            n += 2;
        }

        return 2.0 * result;
    }
}
