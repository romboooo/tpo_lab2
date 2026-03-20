package org.example.logarithms;

import org.example.core.LogarithmicFunction;

public class Log extends LogarithmicFunction {

    private final Ln ln;
    private final double base;
    private final double lnBase;

    public Log(double base){
        super();

        if(base <= 0 || base == 1){
            throw new IllegalArgumentException("Base must be > 0 and ≠ 1");
        }
        this.base = base;
        this.ln = new Ln(epsilon, maxIterations);
        this.lnBase = ln.solve(base);
    }

    public Log(double base, double epsilon, int maxIterations) {
        super(epsilon, maxIterations);
        if (base <= 0 || base == 1) {
            throw new IllegalArgumentException("Base must be > 0 and ≠ 1");
        }
        this.base = base;
        this.ln = new Ln(epsilon, maxIterations);
        this.lnBase = ln.solve(base);
    }
    public Log(Ln ln, double base, double epsilon, int maxIterations) {
        super(epsilon, maxIterations);
        if (base <= 0 || base == 1) {
            throw new IllegalArgumentException("Base must be > 0 and ≠ 1");
        }
        this.base = base;
        this.ln = ln;
        this.lnBase = ln.solve(base);
    }

    @Override
    public double solve(double x) {
        if (!isDefined(x)) {
            return Double.NaN;
        }

        double lnX = ln.solve(x);
        if (Double.isNaN(lnX)) {
            return Double.NaN;
        }

        return lnX / lnBase;
    }

    public double getBase() {
        return base;
    }
    @Override
    public String getName() {
        return "log" + getBase();
    }

    @Override
    public String getDomainDescription() {
        return "x ∈ (0, +∞)";
    }
}
