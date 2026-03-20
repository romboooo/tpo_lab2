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
@DisplayName("Тесты для функции Cos(x)")
class CosTest {

    private static final double EPSILON = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private static final double DELTA = 1e-5;

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data/cos-values.csv", numLinesToSkip = 1)
    @DisplayName("Проверка cos(x) по эталонным значениям из CSV")
    void testCos_FromCsvFile(double x, double expected, String description) {
        Cos cos = new Cos(EPSILON, MAX_ITERATIONS);
        double result = cos.solve(x);

        assertEquals(expected, result, DELTA,
                String.format("FAIL %s | x=%.6f, expected=%.10f, got=%.10f",
                        description, x, expected, result));
    }

    @Test
    @DisplayName("Проверка формулы cos(x) = sin(π/2 - x) с моком Sin")
    void testCos_WithMockedSin() {
        Sin mockSin = mock(Sin.class);

        when(mockSin.solve(Math.PI / 2.0)).thenReturn(1.0);
        when(mockSin.solve(0.0)).thenReturn(0.0);
        when(mockSin.solve(-Math.PI / 2.0)).thenReturn(-1.0);

        Cos cos = new Cos(mockSin, EPSILON, MAX_ITERATIONS);

        double result1 = cos.solve(0.0);
        assertEquals(1.0, result1, DELTA, "cos(0) должен быть 1.0");

        double result2 = cos.solve(Math.PI / 2.0);
        assertEquals(0.0, result2, DELTA, "cos(π/2) должен быть 0.0");

        double result3 = cos.solve(Math.PI);
        assertEquals(-1.0, result3, DELTA, "cos(π) должен быть -1.0");

        verify(mockSin, times(3)).solve(anyDouble());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data/cos-values.csv", numLinesToSkip = 1)
    @DisplayName("Проверка области определения cos(x)")
    void testCos_IsDefined(double x, double expected, String description) {
        Cos cos = new Cos(EPSILON, MAX_ITERATIONS);

        assertTrue(cos.isDefined(x),
                String.format("cos(%.6f) должен быть определён", x));
    }

    @Test
    @DisplayName("Проверка недопустимых значений для cos(x)")
    void testCos_NotDefinedValues() {
        Cos cos = new Cos(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(cos.solve(Double.NaN)));
        assertTrue(Double.isNaN(cos.solve(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(cos.solve(Double.NEGATIVE_INFINITY)));

        assertFalse(cos.isDefined(Double.NaN));
        assertFalse(cos.isDefined(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Проверка периодичности cos(x)")
    void testCos_Periodicity() {
        Cos cos = new Cos(EPSILON, MAX_ITERATIONS);
        double twoPi = 2.0 * Math.PI;

        assertEquals(cos.solve(0.5), cos.solve(0.5 + twoPi), DELTA);
        assertEquals(cos.solve(1.0), cos.solve(1.0 + twoPi), DELTA);
        assertEquals(cos.solve(-0.5), cos.solve(-0.5 + twoPi), DELTA);
    }

    @Test
    @DisplayName("Проверка экстремумов cos(x)")
    void testCos_Extremums() {
        Cos cos = new Cos(EPSILON, MAX_ITERATIONS);

        // Максимум в 0, 2π
        double max1 = cos.solve(0.0);
        assertTrue(max1 >= 0.99999 && max1 <= 1.00001, "cos(0) должен быть ≈ 1");

        double max2 = cos.solve(2.0 * Math.PI);
        assertTrue(max2 >= 0.99999 && max2 <= 1.00001, "cos(2π) должен быть ≈ 1");

        // Минимум в π
        double min = cos.solve(Math.PI);
        assertTrue(min >= -1.00001 && min <= -0.99999, "cos(π) должен быть ≈ -1");
    }

    @Test
    @DisplayName("Проверка getName() и getDomainDescription()")
    void testCos_Metadata() {
        Cos cos = new Cos(EPSILON, MAX_ITERATIONS);

        assertEquals("cos", cos.getName());
        assertEquals("x ∈ ℝ (все действительные числа)", cos.getDomainDescription());
    }

    @Test
    @DisplayName("Проверка экспорта в CSV")
    void testCos_ExportToCsv() throws Exception {
        Cos cos = new Cos(EPSILON, MAX_ITERATIONS);

        java.io.StringWriter writer = new java.io.StringWriter();
        cos.exportToCsv(writer, 0.0, Math.PI, Math.PI / 4.0);

        String csv = writer.toString();
        writer.close();

        assertTrue(csv.contains("x,cos"));
        assertTrue(csv.length() > 20);
        assertFalse(csv.contains("NaN"), "cos(x) для [0,π] не должен возвращать NaN");
    }
}