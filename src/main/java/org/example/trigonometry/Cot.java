package org.example.trigonometry;

import org.example.core.TrigonometricFunction;

public class Cot extends TrigonometricFunction {
    private final Sin sin;
    private final Cos cos;

    public Cot(){
        super();
        this.sin = new Sin(epsilon, maxIterations);
        this.cos = new Cos(epsilon, maxIterations);
    }

    public Cot(double epsilon, int maxIterations){
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

        if(Math.abs(sinVal) < epsilon){
            return Double.NaN;
        }
        return  cosVal / sinVal;
    }

    @Override
    public boolean isDefined(double x) {
        if(Double.isInfinite(x) || Double.isNaN(x)){
            return false;
        }
        return !isDiscontinuityPoint(x, Math.PI, 0.0);
    }

    @Override
    public String getName() {
        return "cot";
    }

    @Override
    public String getDomainDescription() {
        return "x ∈ ℝ, x ≠ πk, k ∈ ℤ";
    }
}
