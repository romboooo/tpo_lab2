package org.example.trigonometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("тесты для функции Sec(x)")
class SecTest {

    private static final double EPSILON = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private static final double DELTA = 1e-4;

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data/sec-values.csv", numLinesToSkip = 1)
    @DisplayName("проверка sec(x) по эталонным значениям из csv")
    void testSec_FromCsvFile(double x, double expected, String description) {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);
        double result = sec.solve(x);

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
    @DisplayName("проверка формулы sec(x) = 1/cos(x) с моком Cos")
    void testSec_WithMockedCos() {
        Cos mockCos = mock(Cos.class);

        when(mockCos.solve(0.0)).thenReturn(1.0);           // cos(0) = 1
        when(mockCos.solve(Math.PI / 3.0)).thenReturn(0.5); // cos(π/3) = 0.5
        when(mockCos.solve(Math.PI)).thenReturn(-1.0);      // cos(π) = -1

        Sec sec = new Sec(mockCos, EPSILON, MAX_ITERATIONS);

        assertEquals(1.0, sec.solve(0.0), DELTA);

        assertEquals(2.0, sec.solve(Math.PI / 3.0), DELTA);

        assertEquals(-1.0, sec.solve(Math.PI), DELTA);

        verify(mockCos, times(3)).solve(anyDouble());
    }

    @Test
    @DisplayName("проверка точек разрыва sec(x) в π/2 + πk")
    void testSec_Discontinuities() {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(sec.solve(Math.PI / 2.0)), "sec(π/2) должен быть NaN");
        assertTrue(Double.isNaN(sec.solve(3.0 * Math.PI / 2.0)), "sec(3π/2) должен быть NaN");
        assertTrue(Double.isNaN(sec.solve(-Math.PI / 2.0)), "sec(-π/2) должен быть NaN");
        assertTrue(Double.isNaN(sec.solve(5.0 * Math.PI / 2.0)), "sec(5π/2) должен быть NaN");

        assertFalse(sec.isDefined(Math.PI / 2.0));
        assertFalse(sec.isDefined(3.0 * Math.PI / 2.0));
    }

    @Test
    @DisplayName("проверка поведения sec(x) рядом с асимптотой")
    void testSec_NearAsymptote() {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);
        double eps = 1e-4;

        double left = Math.PI / 2.0 - eps;
        double resultLeft = sec.solve(left);
        assertTrue(resultLeft > 1000.0,
                String.format("sec(π/2 - ε) должен быть >> 0, получил %.4f", resultLeft));

        double right = Math.PI / 2.0 + eps;
        double resultRight = sec.solve(right);
        assertTrue(resultRight < -1000.0,
                String.format("sec(π/2 + ε) должен быть << 0, получил %.4f", resultRight));
    }

    @Test
    @DisplayName("проверка области определения sec(x)")
    void testSec_IsDefined() {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);

        assertTrue(sec.isDefined(0.0));
        assertTrue(sec.isDefined(Math.PI));
        assertTrue(sec.isDefined(2.0 * Math.PI));

        assertFalse(sec.isDefined(Math.PI / 2.0));
        assertFalse(sec.isDefined(3.0 * Math.PI / 2.0));

        assertFalse(sec.isDefined(Double.NaN));
        assertFalse(sec.isDefined(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("проверка обработки NaN и Infinity")
    void testSec_NotDefinedValues() {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(sec.solve(Double.NaN)));
        assertTrue(Double.isNaN(sec.solve(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(sec.solve(Double.NEGATIVE_INFINITY)));
    }
    @Test
    @DisplayName("проверка периодичности sec(x)")
    void testSec_Periodicity() {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);
        double twoPi = 2.0 * Math.PI;

        assertEquals(sec.solve(0.3), sec.solve(0.3 + twoPi), DELTA);
        assertEquals(sec.solve(1.0), sec.solve(1.0 + twoPi), DELTA);
    }

    @Test
    @DisplayName("проверка экстремумов sec(x)")
    void testSec_Extremums() {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);

        // минимум модуля в x = 0, 2π: |sec| = 1
        double min1 = sec.solve(0.0);
        assertEquals(1.0, min1, DELTA);

        double min2 = sec.solve(2.0 * Math.PI);
        assertEquals(1.0, min2, DELTA);

        // максимум модуля в x = π: sec = -1
        double max = sec.solve(Math.PI);
        assertEquals(-1.0, max, DELTA);
    }

    @Test
    @DisplayName("проверка getName() и getDomainDescription()")
    void testSec_Metadata() {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);

        assertEquals("sec", sec.getName());
        assertEquals("x ∈ ℝ, x ≠ π/2 + πk, k ∈ ℤ", sec.getDomainDescription());
    }

    @Test
    @DisplayName("проверка экспорта в csv")
    void testSec_ExportToCsv() throws Exception {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);

        java.io.StringWriter writer = new java.io.StringWriter();
        sec.exportToCsv(writer, 0.0, Math.PI, Math.PI / 4.0);

        String csv = writer.toString();
        writer.close();

        assertTrue(csv.contains("x,sec"));
        assertTrue(csv.length() > 20);
        assertTrue(csv.contains("NaN"), "sec(x) должен содержать NaN в точке разрыва");
    }

    @ParameterizedTest
    @ValueSource(doubles = {
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.NaN
    })
    @DisplayName("проверка обработки специальных значений")
    void testSin_SpecialValues(double x) {
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(sec.solve(x)),
                String.format("должен возвращать NaN для %.4f", x));
        assertFalse(sec.isDefined(x));
    }
}