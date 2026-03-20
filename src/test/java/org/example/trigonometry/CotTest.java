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
@DisplayName("тесты для функции Cot(x)")
class CotTest {

    private static final double EPSILON = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private static final double DELTA = 1e-4;

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data/cot-values.csv", numLinesToSkip = 1)
    @DisplayName("проверка cot(x) по эталонным значениям из csv")
    void testCot_FromCsvFile(double x, double expected, String description) {
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);
        double result = cot.solve(x);

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
    @DisplayName("проверка формулы cot(x) = cos(x)/sin(x) с моками")
    void testCot_WithMockedSinAndCos() {
        Sin mockSin = mock(Sin.class);
        Cos mockCos = mock(Cos.class);

        when(mockSin.solve(Math.PI / 4.0)).thenReturn(0.7071067812);  // sin(π/4) = √2/2
        when(mockSin.solve(Math.PI / 2.0)).thenReturn(1.0);           // sin(π/2) = 1
        when(mockSin.solve(Math.PI / 6.0)).thenReturn(0.5);           // sin(π/6) = 0.5

        when(mockCos.solve(Math.PI / 4.0)).thenReturn(0.7071067812);  // cos(π/4) = √2/2
        when(mockCos.solve(Math.PI / 2.0)).thenReturn(0.0);           // cos(π/2) = 0
        when(mockCos.solve(Math.PI / 6.0)).thenReturn(0.8660254038);  // cos(π/6) = √3/2

        Cot cot = new Cot(mockSin, mockCos, EPSILON, MAX_ITERATIONS);

        assertEquals(1.0, cot.solve(Math.PI / 4.0), DELTA);

        assertEquals(0.0, cot.solve(Math.PI / 2.0), DELTA);

        assertEquals(1.7320508076, cot.solve(Math.PI / 6.0), DELTA);

        verify(mockSin, times(3)).solve(anyDouble());
        verify(mockCos, times(3)).solve(anyDouble());
    }

    @Test
    @DisplayName("проверка точек разрыва cot(x) в πk")
    void testCot_Discontinuities() {
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(cot.solve(0.0)), "cot(0) должен быть NaN");
        assertTrue(Double.isNaN(cot.solve(Math.PI)), "cot(π) должен быть NaN");
        assertTrue(Double.isNaN(cot.solve(2.0 * Math.PI)), "cot(2π) должен быть NaN");
        assertTrue(Double.isNaN(cot.solve(-Math.PI)), "cot(-π) должен быть NaN");

        assertFalse(cot.isDefined(0.0));
        assertFalse(cot.isDefined(Math.PI));
        assertFalse(cot.isDefined(2.0 * Math.PI));
    }

    @Test
    @DisplayName("проверка поведения cot(x) рядом с асимптотой")
    void testCot_NearAsymptote() {
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);
        double eps = 1e-4;

        // справа от 0: sin > 0, cos ≈ 1, значит cot → +∞
        double right = 0.0 + eps;
        double resultRight = cot.solve(right);
        assertTrue(resultRight > 1000.0,
                String.format("cot(0 + ε) должен быть >> 0, получил %.4f", resultRight));

        // слева от 0: sin < 0, cos ≈ 1, значит cot → -∞
        double left = 0.0 - eps;
        double resultLeft = cot.solve(left);
        assertTrue(resultLeft < -1000.0,
                String.format("cot(0 - ε) должен быть << 0, получил %.4f", resultLeft));
    }

    // тест 5: нули функции (где cos = 0)
    @Test
    @DisplayName("проверка нулей cot(x) в π/2 + πk")
    void testCot_Zeros() {
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);

        // cot = 0 там, где cos = 0: π/2, 3π/2, -π/2, etc.
        assertEquals(0.0, cot.solve(Math.PI / 2.0), DELTA);
        assertEquals(0.0, cot.solve(3.0 * Math.PI / 2.0), DELTA);
        assertEquals(0.0, cot.solve(-Math.PI / 2.0), DELTA);
    }

    @Test
    @DisplayName("проверка области определения cot(x)")
    void testCot_IsDefined() {
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);

        assertTrue(cot.isDefined(Math.PI / 4.0));
        assertTrue(cot.isDefined(Math.PI / 2.0));
        assertTrue(cot.isDefined(Math.PI / 6.0));

        assertFalse(cot.isDefined(0.0));
        assertFalse(cot.isDefined(Math.PI));
        assertFalse(cot.isDefined(2.0 * Math.PI));

        assertFalse(cot.isDefined(Double.NaN));
        assertFalse(cot.isDefined(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("проверка обработки NaN и Infinity")
    void testCot_NotDefinedValues() {
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(cot.solve(Double.NaN)));
        assertTrue(Double.isNaN(cot.solve(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(cot.solve(Double.NEGATIVE_INFINITY)));
    }

    // (период = π)
    @Test
    @DisplayName("проверка периодичности cot(x) (период = π)")
    void testCot_Periodicity() {
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);
        double pi = Math.PI;

        assertEquals(cot.solve(Math.PI / 6.0), cot.solve(Math.PI / 6.0 + pi), DELTA);
        assertEquals(cot.solve(Math.PI / 4.0), cot.solve(Math.PI / 4.0 + pi), DELTA);
        assertEquals(cot.solve(1.0), cot.solve(1.0 + pi), DELTA);
    }

    @Test
    @DisplayName("проверка getName() и getDomainDescription()")
    void testCot_Metadata() {
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);

        assertEquals("cot", cot.getName());
        assertEquals("x ∈ ℝ, x ≠ πk, k ∈ ℤ", cot.getDomainDescription());
    }

    @Test
    @DisplayName("проверка экспорта в csv")
    void testCot_ExportToCsv() throws Exception {
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);

        java.io.StringWriter writer = new java.io.StringWriter();
        cot.exportToCsv(writer, 0.1, Math.PI - 0.1, Math.PI / 4.0);

        String csv = writer.toString();
        writer.close();

        System.out.println("\n=== Cot CSV OUTPUT ===\n" + csv + "\n=== END ===\n");

        assertTrue(csv.contains("x,cot"));
        assertTrue(csv.length() > 20);
    }
}