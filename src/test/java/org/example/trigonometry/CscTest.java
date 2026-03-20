package org.example.trigonometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("тесты для функции Csc(x)")
class CscTest {

    private static final double EPSILON = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private static final double DELTA = 1e-4;

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data/csc-values.csv", numLinesToSkip = 1)
    @DisplayName("проверка csc(x) по эталонным значениям из csv")
    void testCsc_FromCsvFile(double x, double expected, String description) {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);
        double result = csc.solve(x);

        if (Double.isNaN(expected)) {
            assertTrue(Double.isNaN(result),
                    String.format("FAIL %s | x=%.6f должен быть NaN", description, x));
        } else {
            assertEquals(expected, result, DELTA,
                    String.format("FAIL %s | x=%.6f, expected=%.10f, got=%.10f",
                            description, x, expected, result));
        }
    }

    @Test
    @DisplayName("проверка формулы csc(x) = 1/sin(x) с моком Sin")
    void testCsc_WithMockedSin() {
        // создаём мок для Sin
        Sin mockSin = mock(Sin.class);

        when(mockSin.solve(Math.PI / 2.0)).thenReturn(1.0);    // sin(π/2) = 1
        when(mockSin.solve(Math.PI / 6.0)).thenReturn(0.5);    // sin(π/6) = 0.5
        when(mockSin.solve(-Math.PI / 2.0)).thenReturn(-1.0);  // sin(-π/2) = -1

        Csc csc = new Csc(mockSin, EPSILON, MAX_ITERATIONS);

        assertEquals(1.0, csc.solve(Math.PI / 2.0), DELTA);

        assertEquals(2.0, csc.solve(Math.PI / 6.0), DELTA);

        assertEquals(-1.0, csc.solve(-Math.PI / 2.0), DELTA);

        verify(mockSin, times(3)).solve(anyDouble());
    }

    @Test
    @DisplayName("проверка точек разрыва csc(x) в πk")
    void testCsc_Discontinuities() {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(csc.solve(0.0)), "csc(0) должен быть NaN");
        assertTrue(Double.isNaN(csc.solve(Math.PI)), "csc(π) должен быть NaN");
        assertTrue(Double.isNaN(csc.solve(2.0 * Math.PI)), "csc(2π) должен быть NaN");
        assertTrue(Double.isNaN(csc.solve(-Math.PI)), "csc(-π) должен быть NaN");

        assertFalse(csc.isDefined(0.0));
        assertFalse(csc.isDefined(Math.PI));
        assertFalse(csc.isDefined(2.0 * Math.PI));
    }

    @Test
    @DisplayName("проверка поведения csc(x) рядом с асимптотой")
    void testCsc_NearAsymptote() {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);
        double eps = 1e-4;

        // справа от 0: sin > 0, значит csc → +∞
        double right = 0.0 + eps;
        double resultRight = csc.solve(right);
        assertTrue(resultRight > 1000.0,
                String.format("csc(0 + ε) должен быть >> 0, получил %.4f", resultRight));

        // слева от 0: sin < 0, значит csc → -∞
        double left = 0.0 - eps;
        double resultLeft = csc.solve(left);
        assertTrue(resultLeft < -1000.0,
                String.format("csc(0 - ε) должен быть << 0, получил %.4f", resultLeft));
    }

    @Test
    @DisplayName("проверка области определения csc(x)")
    void testCsc_IsDefined() {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);

        assertTrue(csc.isDefined(Math.PI / 2.0));
        assertTrue(csc.isDefined(Math.PI / 6.0));
        assertTrue(csc.isDefined(3.0 * Math.PI / 2.0));

        assertFalse(csc.isDefined(0.0));
        assertFalse(csc.isDefined(Math.PI));
        assertFalse(csc.isDefined(2.0 * Math.PI));

        assertFalse(csc.isDefined(Double.NaN));
        assertFalse(csc.isDefined(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("проверка обработки NaN и Infinity")
    void testCsc_NotDefinedValues() {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(csc.solve(Double.NaN)));
        assertTrue(Double.isNaN(csc.solve(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(csc.solve(Double.NEGATIVE_INFINITY)));
    }

    //  (период = 2π)
    @Test
    @DisplayName("проверка периодичности csc(x)")
    void testCsc_Periodicity() {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);
        double twoPi = 2.0 * Math.PI;

        // csc(x) = csc(x + 2π) для регулярных точек
        assertEquals(csc.solve(Math.PI / 6.0), csc.solve(Math.PI / 6.0 + twoPi), DELTA);
        assertEquals(csc.solve(Math.PI / 3.0), csc.solve(Math.PI / 3.0 + twoPi), DELTA);
    }

    @Test
    @DisplayName("проверка экстремумов csc(x)")
    void testCsc_Extremums() {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);

        // минимум в π/2: csc = 1
        double min = csc.solve(Math.PI / 2.0);
        assertEquals(1.0, min, DELTA);

        // максимум в 3π/2: csc = -1
        double max = csc.solve(3.0 * Math.PI / 2.0);
        assertEquals(-1.0, max, DELTA);
    }

    @Test
    @DisplayName("проверка getName() и getDomainDescription()")
    void testCsc_Metadata() {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);

        assertEquals("csc", csc.getName());
        assertEquals("x ∈ ℝ, x ≠ πk, k ∈ ℤ", csc.getDomainDescription());
    }

    @Test
    @DisplayName("проверка экспорта в csv")
    void testCsc_ExportToCsv() throws Exception {
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);

        java.io.StringWriter writer = new java.io.StringWriter();
        csc.exportToCsv(writer, 0.1, Math.PI - 0.1, Math.PI / 4.0);

        String csv = writer.toString();
        writer.close();

        System.out.println("\n=== Csc CSV OUTPUT ===\n" + csv + "\n=== END ===\n");

        assertTrue(csv.contains("x,csc"));
        assertTrue(csv.length() > 20);
    }
}