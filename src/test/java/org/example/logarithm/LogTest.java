package org.example.logarithm;

import org.example.logarithms.Ln;
import org.example.logarithms.Log;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для функции Log_b(x)")
class LogTest {

    private static final double EPSILON = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private static final double DELTA = 1e-4;

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data/log-values.csv", numLinesToSkip = 1)
    @DisplayName("Проверка log_b(x) по эталонным значениям из CSV")
    void testLog_FromCsvFile(double base, double x, double expected, String description) {
        Log log = new Log(base, EPSILON, MAX_ITERATIONS);
        double result = log.solve(x);

        assertEquals(expected, result, DELTA,
                String.format("FAIL %s | log_%.1f(%.4f) = %.10f, expected %.10f",
                        description, base, x, result, expected));
    }

    @Test
    @DisplayName("Проверка формулы log_b(x) = ln(x)/ln(b) с моком Ln")
    void testLog_WithMockedLn() {
        Ln mockLn = mock(Ln.class);

        when(mockLn.solve(8.0)).thenReturn(2.0794415417);
        when(mockLn.solve(2.0)).thenReturn(0.6931471806);

        Log log = new Log(mockLn, 2.0, EPSILON, MAX_ITERATIONS);

        double result = log.solve(8.0);
        double expected = 2.0794415417 / 0.6931471806; // ≈ 3.0

        assertEquals(expected, result, DELTA, "log2(8) должен быть ≈ 3");

        verify(mockLn, times(2)).solve(anyDouble());
    }

    @Test
    @DisplayName("Проверка недопустимых значений для log_b(x)")
    void testLog_NotDefinedValues() {
        Log log = new Log(10.0, EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(log.solve(0.0)));
        assertTrue(Double.isNaN(log.solve(-5.0)));
        assertTrue(Double.isNaN(log.solve(Double.NaN)));

        assertFalse(log.isDefined(0.0));
        assertFalse(log.isDefined(-1.0));
    }

    @Test
    @DisplayName("Проверка исключения при недопустимом основании")
    void testLog_InvalidBase() {
        assertThrows(IllegalArgumentException.class, () -> new Log(0.0));
        assertThrows(IllegalArgumentException.class, () -> new Log(1.0));
        assertThrows(IllegalArgumentException.class, () -> new Log(-2.0));
    }

    @Test
    @DisplayName("Проверка getName() и getBase()")
    void testLog_Metadata() {
        Log log2 = new Log(2.0, EPSILON, MAX_ITERATIONS);
        assertEquals("log2.0", log2.getName());
        assertEquals(2.0, log2.getBase());

        Log log10 = new Log(10.0, EPSILON, MAX_ITERATIONS);
        assertEquals("log10.0", log10.getName());
    }
}