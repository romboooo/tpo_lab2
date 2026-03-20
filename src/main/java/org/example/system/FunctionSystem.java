package org.example.system;

import org.example.logarithms.*;
import org.example.trigonometry.*;

import static java.lang.Math.*;

public class FunctionSystem {
    private final Csc csc;
    private final Sin sin;
    private final Sec sec;
    private final Cot cot;
    private final Ln ln;
    private final Log log2;
    private final Log log5;
    private final Log log10;

    public FunctionSystem(Sin sin, Sec sec, Cot cot, Csc csc, Ln ln,
                          Log log2, Log log5, Log log10) {
        this.sin = sin;
        this.sec = sec;
        this.cot = cot;
        this.csc = csc;
        this.ln = ln;
        this.log2 = log2;
        this.log5 = log5;
        this.log10 = log10;
    }

    public FunctionSystem(double epsilon, int maxIterations) {
        this.sin = new Sin(epsilon, maxIterations);
        this.sec = new Sec(epsilon, maxIterations);
        this.cot = new Cot(epsilon, maxIterations);
        this.csc = new Csc(epsilon, maxIterations);

        this.ln = new Ln(epsilon, maxIterations);
        this.log2 = new Log(2.0, epsilon, maxIterations);
        this.log5 = new Log(5.0, epsilon, maxIterations);
        this.log10 = new Log(10.0, epsilon, maxIterations);
    }

    public double solve(double x) {
        return (x <= 0) ? solveFirstBranch(x) : solveSecondBranch(x);
    }

    public double solveFirstBranch(double x) {
        if (!sin.isDefined(x) || !csc.isDefined(x) ||
                !sec.isDefined(x) || !cot.isDefined(x)) {
            return Double.NaN;
        }

        double firstTerm = pow(pow((csc.solve(x) / sin.solve(x)), 2) * sec.solve(x), 3);
        double secondTerm = cot.solve(x) + pow(sec.solve(x), 6);
        return firstTerm - secondTerm;
    }

    public double solveSecondBranch(double x) {
        return ((pow(ln.solve(x), 2) * log2.solve(x)
                + log2.solve(x))
                + log10.solve(x))
                - log5.solve(x);
    }
}
