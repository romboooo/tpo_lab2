package org.example.trigonometry;

import org.example.core.TrigonometricFunction;

public class Tan extends TrigonometricFunction {

    private final Sin sin;
    private final Cos cos;

    public Tan(){
        super();
        this.sin = new Sin(epsilon, maxIterations);
        this.cos = new Cos(epsilon, maxIterations);
    }

    public Tan(double epsilon, int maxIterations){
        super();
        this.sin = new Sin(epsilon, maxIterations);
        this.cos = new Cos(epsilon, maxIterations);
    }

    @Override
    public double solve(double x) {
        if(!isDefined(x)){
            return Double.NaN;
        }

        double sinVal = sin.solve(x);
        double cosVal = cos.solve(x);

        if(Math.abs(cosVal) < epsilon){
            return Double.NaN;
        }
        return sinVal / cosVal;
    }

    @Override
    public boolean isDefined(double x) {
        if(Double.isInfinite(x) || Double.isNaN(x)){
            return false;
        }
        return !isDiscontinuityPoint(x, Math.PI, Math.PI / 2.0);
    }

    @Override
    public String getName() {
        return "tan";
    }

    @Override
    public String getDomainDescription() {
        return "x ∈ ℝ, x ≠ π/2 + πk, k ∈ ℤ";
    }
}
