package org.example.trigonometry;
import java.lang.Math;
import org.example.core.TrigonometricFunction;

import static java.lang.Math.*;

public class Sec extends TrigonometricFunction {

    private final Cos cos;

    public Sec(){
        super();
        this.cos = new Cos(epsilon, maxIterations);
    }

    public Sec(double epsilon, int maxIterations){
        super();
        this.cos = new Cos(epsilon, maxIterations);
    }

    @Override
    public double solve(double x) {

        if(!isDefined(x)){
            return Double.NaN;
        }

        double cosVal = cos.solve(x);

        if(abs(cosVal) < epsilon){
            return Double.NaN;
        }
        return 1.0 / cosVal;
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
        return "sec";
    }

    @Override
    public String getDomainDescription() {
        return "x ∈ ℝ, x ≠ π/2 + πk, k ∈ ℤ";
    }
}
