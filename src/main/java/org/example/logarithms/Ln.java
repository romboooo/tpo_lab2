package org.example.logarithms;

import org.example.core.LogarithmicFunction;

public class Ln extends LogarithmicFunction {

    private static final double LN_2;

    static {
        Ln initializer = new Ln(1e-15, 500);
        LN_2 = initializer.computeLnRaw(2.0);
    }

    public Ln(){
        super();
    }

    public Ln(double epsilon, int maxIterations){
        super(epsilon, maxIterations);
    }
    @Override
    public double solve(double x) {
        if(!isDefined(x)){
            return Double.NaN;
        }

        double[] norm = normalizeArgument(x);
        double normalizedX = norm[0];
        int k = (int) norm[1];

        double result = computeLnNormalized(normalizedX);

        if(k!=0){
            result += k * LN_2;
        }
        return result;
    }


    private double computeLnRaw(double x){
        if(x <= 0){
            return Double.NaN;
        }

        double y = (x - 1.0) / (x + 1.0);

        double result = 0.0;
        double term = y;
        int n = 1;

        double localEps = 1e-15;
        while (Math.abs(term / n) > localEps){
            if (n >= 1000) {
                break;
            }
            result += term / n;
            term *= y*y;
            n += 2;
        }

        return 2.0 * result;
    }

    @Override
    public String getName() {
        return "ln";
    }

    @Override
    public String getDomainDescription() {
        return "x ∈ (0, +∞)";
    }
}
