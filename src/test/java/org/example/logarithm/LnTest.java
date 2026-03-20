package org.example.logarithm;

import org.example.logarithms.Ln;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для функции Ln(x)")
class LnTest {

    private static final double EPSILON = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private static final double DELTA = 1e-4;

    @ParameterizedTest
    @CsvFileSource(
            resources = "/test-data/ln-values.csv",
            numLinesToSkip = 1,
            lineSeparator = "\n"
    )
    @DisplayName("Проверка ln(x) по эталонным значениям из CSV")
    void testLn_FromCsvFile(double x, double expected, String description) {
        Ln ln = new Ln(EPSILON, MAX_ITERATIONS);
        double result = ln.solve(x);

        assertEquals(expected, result, DELTA,
                String.format("fail %s | x=%.6f, expected=%.10f, got=%.10f",
                        description, x, expected, result));
    }

    @ParameterizedTest
    @CsvFileSource(
            resources = "/test-data/ln-values.csv",
            numLinesToSkip = 1,
            lineSeparator = "\n"
    )
    @DisplayName("Проверка области определения ln(x)")
    void testLn_IsDefined(double x, double expected, String description) {
        Ln ln = new Ln(EPSILON, MAX_ITERATIONS);
        // x > 0
        assertTrue(ln.isDefined(x),
                String.format("ln(%.6f) должен быть определён", x));
    }

    @Test
    @DisplayName("Проверка недопустимых значений для ln(x)")
    void testLn_NotDefinedValues() {
        Ln ln = new Ln(EPSILON, MAX_ITERATIONS);

        // x <= 0 → не определено
        assertTrue(Double.isNaN(ln.solve(0.0)));
        assertTrue(Double.isNaN(ln.solve(-1.0)));
        assertTrue(Double.isNaN(ln.solve(-100.0)));
        assertTrue(Double.isNaN(ln.solve(Double.NaN)));
        assertTrue(Double.isNaN(ln.solve(Double.POSITIVE_INFINITY)));

        assertFalse(ln.isDefined(0.0));
        assertFalse(ln.isDefined(-1.0));
        assertFalse(ln.isDefined(Double.NaN));
    }

    @Test
    @DisplayName("Проверка поведения ln(x) при x → 0+")
    void testLn_NearZero() {
        Ln ln = new Ln(EPSILON, MAX_ITERATIONS);

        // При x → 0+ значение должно стремиться к -∞
        double small1 = ln.solve(0.001);
        double small2 = ln.solve(0.0001);

        assertTrue(small1 < -6.0, "ln(0.001) должен быть < -6");
        assertTrue(small2 < -9.0, "ln(0.0001) должен быть < -9");
        assertTrue(small2 < small1, "ln(x) должен уменьшаться при x → 0+");
    }

    @Test
    @DisplayName("Проверка свойства ln(a*b) = ln(a) + ln(b)")
    void testLn_ProductProperty() {
        Ln ln = new Ln(EPSILON, MAX_ITERATIONS);

        double a = 2.0;
        double b = 3.0;

        double lnA = ln.solve(a);
        double lnB = ln.solve(b);
        double lnAB = ln.solve(a * b);

        assertEquals(lnA + lnB, lnAB, DELTA,
                "ln(a*b) должен равняться ln(a) + ln(b)");
    }

    @Test
    @DisplayName("Проверка свойства ln(a/b) = ln(a) - ln(b)")
    void testLn_QuotientProperty() {
        Ln ln = new Ln(EPSILON, MAX_ITERATIONS);

        double a = 10.0;
        double b = 2.0;

        double lnA = ln.solve(a);
        double lnB = ln.solve(b);
        double lnAB = ln.solve(a / b);

        assertEquals(lnA - lnB, lnAB, DELTA,
                "ln(a/b) должен равняться ln(a) - ln(b)");
    }

    @Test
    @DisplayName("Проверка getName() и getDomainDescription()")
    void testLn_Metadata() {
        Ln ln = new Ln(EPSILON, MAX_ITERATIONS);

        assertEquals("ln", ln.getName());
        assertEquals("x ∈ (0, +∞)", ln.getDomainDescription());
    }

    @Test
    @DisplayName("Проверка экспорта в CSV")
    void testLn_ExportToCsv() throws Exception {
        Ln ln = new Ln(EPSILON, MAX_ITERATIONS);

        java.io.StringWriter writer = new java.io.StringWriter();
        ln.exportToCsv(writer, 1.0, 3.0, 1.0);

        String csv = writer.toString();
        writer.close();

        assertTrue(csv.contains("x,ln"), "CSV должен содержать заголовок 'x,ln'");

        assertTrue(csv.length() > 20, "CSV должен содержать данные, а не только заголовок");

        assertFalse(csv.contains("NaN"), "ln(x) для x=[1,3] не должен возвращать NaN");
    }
}