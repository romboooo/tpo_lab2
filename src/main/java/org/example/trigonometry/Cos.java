package org.example.trigonometry;

import org.example.core.TrigonometricFunction;

public class Cos extends TrigonometricFunction {

    private final Sin sin;

    public Cos(){
        super();
        this.sin = new Sin(epsilon, maxIterations);
    }

    public Cos(double epsilon, int maxIterations){
        super(epsilon,maxIterations);
        this.sin = new Sin(epsilon,maxIterations);
    }

    @Override
    public double solve(double x) {

        if(!isDefined(x)){
            return Double.NaN;
        }

        double result = sin.solve(Math.PI / 2.0 - x);
        return clampToTrigRange(result);
    }

    @Override
    public boolean isDefined(double x) {
        return !Double.isInfinite(x) && !Double.isNaN(x);
    }

    @Override
    public String getName() {
        return "cos";
    }

    @Override
    public String getDomainDescription() {
        return "x ∈ ℝ (все действительные числа)";
    }
}



