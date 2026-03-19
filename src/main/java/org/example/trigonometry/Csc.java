package org.example.trigonometry;

import org.example.core.TrigonometricFunction;
import static java.lang.Math.*;

public class Csc extends TrigonometricFunction {

    private final Sin sin;

    public Csc(){
        super();
        this.sin = new Sin(epsilon, maxIterations);
    }

    public Csc(double epsilon, int maxIterations){
        super();
        this.sin = new Sin(epsilon, maxIterations);
    }

    @Override
    public double solve(double x) {

        if(!isDefined(x)){
            return Double.NaN;
        }
        double sinVal = sin.solve(x);

        if(abs(sinVal) < epsilon){
            return Double.NaN;
        }
        return 1.0 / sinVal;
    }

    @Override
    public boolean isDefined(double x) {
        if (Double.isInfinite(x) || Double.isNaN(x)) {
            return false;
        }
        return !isDiscontinuityPoint(x, Math.PI, 0.0);
    }

    @Override
    public String getName() {
        return "csc";
    }

    @Override
    public String getDomainDescription() {
        return "x ∈ ℝ, x ≠ πk, k ∈ ℤ";
    }
}
