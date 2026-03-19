package org.example;

import org.example.core.LogarithmicFunction;
import org.example.core.TrigonometricFunction;
import org.example.logarithms.Ln;
import org.example.logarithms.Log;
import org.example.trigonometry.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n=== DEBUG NORMALIZATION ===");
        Ln debugLn = new Ln(1e-6, 100);

// x = 8: должно нормализоваться до 1.0, k = 3
        System.out.printf("ln(8) = %.6f (ожидаемо: %.6f)%n",
                debugLn.solve(8.0), Math.log(8));

// x = 0.25: должно нормализоваться до 1.0, k = -2
        System.out.printf("ln(0.25) = %.6f (ожидаемо: %.6f)%n",
                debugLn.solve(0.25), Math.log(0.25));

// Проверка константы
        System.out.printf("LN_2 (внутренняя) ≈ %.15f (эталон: %.15f)%n",
                debugLn.solve(2.0), Math.log(2));
    }
}