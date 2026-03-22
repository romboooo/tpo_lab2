package org.example;

import org.example.logarithms.Ln;
import org.example.logarithms.Log;
import org.example.trigonometry.*;
import java.io.IOException;

public class Main {
    private static final double EPSILON = 1e-6;
    private static final int MAX_ITERATIONS = 100;

    public static void main(String[] args) {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);
        Sin sin = new Sin(EPSILON, MAX_ITERATIONS);
        Cos cos = new Cos(EPSILON, MAX_ITERATIONS);
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);
        Ln ln = new Ln();
        Log log2 = new Log(2,EPSILON, MAX_ITERATIONS);
        Log log5 = new Log(5,EPSILON, MAX_ITERATIONS);
        try {
            csc.exportToCsv("build/reports/csc.csv", -2 * Math.PI, 2 * Math.PI, Math.PI/4);
            sin.exportToCsv("build/reports/sin.csv", -2 * Math.PI, 2 * Math.PI, Math.PI/4);
            cos.exportToCsv("build/reports/cos.csv", -2 * Math.PI, 2 * Math.PI, Math.PI/4);
            sec.exportToCsv("build/reports/sec.csv", -2 * Math.PI, 2 * Math.PI, Math.PI/4);
            cot.exportToCsv("build/reports/cot.csv", -2 * Math.PI, 2 * Math.PI, Math.PI/4);
            ln.exportToCsv("build/reports/ln.csv", 0, 2.71 * 5, 2.71);
            log2.exportToCsv("build/reports/log2.csv", -1, 5, 1);
            log5.exportToCsv("build/reports/log5.csv", -1, 5, 1);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}