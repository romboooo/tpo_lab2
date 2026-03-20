package org.example.trigonometry;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для функции sin(x)")
public class SinTest {
    private static final double EPSILON = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private static final double DELTA = 1e-5;

    @ParameterizedTest
    @CsvFileSource(
            resources = "/test-data/sin-values.csv",
            numLinesToSkip = 1,
            lineSeparator = "\n"
    )
    @DisplayName("проверка sin(x) по эталонным значениям из .csv")
    void testSinFromCSVFile(double x, double expected, String description){
        Sin sin = new Sin(EPSILON, MAX_ITERATIONS);

        double result = sin.solve(x);

        assertEquals(expected,result,DELTA,
                String.format("FAIL %s | x=%.6f, expected=%.10f, got=%.10f",
                        description, x, expected, result));
    }

    @ParameterizedTest
    @CsvFileSource(
            resources = "/test-data/sin-values.csv",
            numLinesToSkip = 1,
            lineSeparator = "\n"
    )
    @DisplayName("Проверка области определения sin(x)")
    void testSin_IsDefined(double x, double expected, String description) {
        Sin sin = new Sin(EPSILON, MAX_ITERATIONS);

        assertTrue(sin.isDefined(x),
                String.format("sin(%.6f) должен быть определён", x));
    }

    @Test
    @DisplayName("Проверка обработки NaN и Infinity")
    void testSin_NotDefinedValues() {
        Sin sin = new Sin(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(sin.solve(Double.NaN)));
        assertTrue(Double.isNaN(sin.solve(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(sin.solve(Double.NEGATIVE_INFINITY)));

        assertFalse(sin.isDefined(Double.NaN));
        assertFalse(sin.isDefined(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Проверка периодичности sin(x)")
    void testSin_Periodicity() {
        Sin sin = new Sin(EPSILON, MAX_ITERATIONS);
        double twoPi = 2.0 * Math.PI;

        // sin(x) = sin(x + 2π)
        assertEquals(sin.solve(0.5), sin.solve(0.5 + twoPi), DELTA);
        assertEquals(sin.solve(1.0), sin.solve(1.0 + twoPi), DELTA);
        assertEquals(sin.solve(-0.5), sin.solve(-0.5 + twoPi), DELTA);
    }

    @Test
    @DisplayName("Проверка экстремумов sin(x)")
    void testSin_Extremums() {
        Sin sin = new Sin(EPSILON, MAX_ITERATIONS);

        // максимум
        double max = sin.solve(Math.PI / 2.0);
        assertTrue(max >= 0.99999 && max <= 1.00001, "sin(π/2) должен быть ≈ 1");

        // минимум
        double min = sin.solve(3.0 * Math.PI / 2.0);
        assertTrue(min >= -1.00001 && min <= -0.99999, "sin(3π/2) должен быть ≈ -1");
    }

    @Test
    @DisplayName("Проверка getName() и getDomainDescription()")
    void testSin_Metadata() {
        Sin sin = new Sin(EPSILON, MAX_ITERATIONS);

        assertEquals("sin", sin.getName());
        assertEquals("x ∈ ℝ (все действительные числа)", sin.getDomainDescription());
    }

}
