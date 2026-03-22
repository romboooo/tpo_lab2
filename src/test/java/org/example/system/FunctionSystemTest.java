package org.example.system;

import org.example.logarithms.Ln;
import org.example.logarithms.Log;
import org.example.trigonometry.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("тесты для FunctionSystem")
class FunctionSystemTest {

    private static final double EPSILON = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private static final double DELTA = 1e-1;

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data/system-values.csv", numLinesToSkip = 1)
    @DisplayName("проверка FunctionSystem по эталонным значениям из csv")
    void testSystem_FromCsvFile(double x, double expected, String branch, String description) {
        FunctionSystem system = new FunctionSystem(EPSILON, MAX_ITERATIONS);
        double result = system.solve(x);

        if (Double.isNaN(expected)) {
            assertTrue(Double.isNaN(result),
                    String.format("FAIL %s | x=%.6f должен быть NaN", description, x));
            return;
        }

        if (Math.abs(expected) > 1e4) {
            return;
        }

        if (Double.isNaN(result)) {
            fail(String.format("FAIL %s | x=%.6f ожидалось %.4f, получил NaN",
                    description, x, expected));
        }

        assertEquals(expected, result, DELTA,
                String.format("FAIL %s | x=%.6f, expected=%.4f, got=%.4f",
                        description, x, expected, result));
    }

    @Test
    @DisplayName("проверка переключения веток (x <= 0 vs x > 0)")
    void testSystem_BranchSwitching() {
        FunctionSystem system = new FunctionSystem(EPSILON, MAX_ITERATIONS);

        assertDoesNotThrow(() -> system.solve(-1.0));
        assertDoesNotThrow(() -> system.solve(0.0));
        assertDoesNotThrow(() -> system.solve(0.1));
        assertDoesNotThrow(() -> system.solve(10.0));
    }

    @Test
    @DisplayName("проверка первой ветки (тригонометрия) с моками")
    void testSystem_FirstBranchWithMocks() {
        Sin mockSin = mock(Sin.class);
        Cos mockCos = mock(Cos.class);

        lenient().when(mockSin.solve(anyDouble())).thenReturn(0.7071067812);
        lenient().when(mockCos.solve(anyDouble())).thenReturn(0.7071067812);

        Sec mockSec = new Sec(mockCos, EPSILON, MAX_ITERATIONS);
        Csc mockCsc = new Csc(mockSin, EPSILON, MAX_ITERATIONS);
        Cot mockCot = new Cot(mockSin, mockCos, EPSILON, MAX_ITERATIONS);

        Ln ln = new Ln(EPSILON, MAX_ITERATIONS);
        Log log2 = new Log(2.0, EPSILON, MAX_ITERATIONS);
        Log log5 = new Log(5.0, EPSILON, MAX_ITERATIONS);
        Log log10 = new Log(10.0, EPSILON, MAX_ITERATIONS);

        FunctionSystem system = new FunctionSystem(
                mockSin, mockSec, mockCot, mockCsc, ln, log2, log5, log10
        );

        assertDoesNotThrow(() -> system.solve(-Math.PI / 4.0),
                "первая ветка должна работать с моками без исключений");
    }

    @Test
    @DisplayName("проверка второй ветки (логарифмы) с моками")
    void testSystem_SecondBranchWithMocks() {
        Ln mockLn = mock(Ln.class);
        Log mockLog2 = mock(Log.class);
        Log mockLog5 = mock(Log.class);
        Log mockLog10 = mock(Log.class);

        lenient().when(mockLn.solve(anyDouble())).thenReturn(1.0);
        lenient().when(mockLog2.solve(anyDouble())).thenReturn(1.0);
        lenient().when(mockLog5.solve(anyDouble())).thenReturn(1.0);
        lenient().when(mockLog10.solve(anyDouble())).thenReturn(1.0);

        Sin sin = new Sin(EPSILON, MAX_ITERATIONS);
        Sec sec = new Sec(EPSILON, MAX_ITERATIONS);
        Cot cot = new Cot(EPSILON, MAX_ITERATIONS);
        Csc csc = new Csc(EPSILON, MAX_ITERATIONS);

        FunctionSystem system = new FunctionSystem(
                sin, sec, cot, csc, mockLn, mockLog2, mockLog5, mockLog10
        );

        assertDoesNotThrow(() -> system.solve(2.0),
                "вторая ветка должна работать с моками без исключений");
    }

    @Test
    @DisplayName("проверка граничной точки x = 0")
    void testSystem_BoundaryPoint() {
        FunctionSystem system = new FunctionSystem(EPSILON, MAX_ITERATIONS);
        double result = system.solve(0.0);
        assertTrue(Double.isNaN(result), "system.solve(0) должен быть NaN (разрыв)");
    }

    @Test
    @DisplayName("проверка обработки NaN от подфункций")
    void testSystem_NaNPropagation() {
        FunctionSystem system = new FunctionSystem(EPSILON, MAX_ITERATIONS);

        assertTrue(Double.isNaN(system.solve(-Math.PI)));
        assertTrue(Double.isNaN(system.solve(-2*Math.PI)));
        assertTrue(Double.isNaN(system.solve(-3*Math.PI/2)));
        assertTrue(Double.isNaN(system.solve(-Math.PI / 2.0)));
        assertTrue(Double.isNaN(system.solve(0.0)));

        assertFalse(Double.isNaN(system.solve(1.0)));
        assertFalse(Double.isNaN(system.solve(2.0)));
    }

    @ParameterizedTest
    @CsvSource({
            // start, end, step, expectedMinLines, shouldContainNaN, description
            "-3.1415926536, 0.0, 1.0, 4, true, 'первая ветка с разрывами'",
            "0.1, 5.0, 1.0, 5, false, 'вторая ветка логарифмы'",
            "-1.0, 1.0, 0.5, 4, true, 'переход через границу веток'",
            "1.0, 10.0, 2.0, 5, false, 'большие значения логарифмов'"
    })
    @DisplayName("проверка экспорта в csv с разными параметрами")
    void testSystem_ExportToCsv(double start, double end, double step,
                                int expectedMinLines, boolean shouldContainNaN) throws Exception {
        FunctionSystem system = new FunctionSystem(EPSILON, MAX_ITERATIONS);

        java.io.StringWriter writer = new java.io.StringWriter();
        writer.write("x,result\n");

        for (double x = start; x <= end + step / 2; x += step) {
            double result = system.solve(x);
            String value = Double.isNaN(result) ? "NaN" :
                    String.format(java.util.Locale.US, "%.10f", result);
            writer.write(String.format(java.util.Locale.US, "%.6f,%s\n", x, value));
        }

        String csv = writer.toString();
        writer.close();

        assertTrue(csv.startsWith("x,result"));

        long lineCount = csv.trim().split("\n").length;
        assertTrue(lineCount >= expectedMinLines);

        if (shouldContainNaN) {
            assertTrue(csv.contains("NaN"));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "-100.0, first",
            "-1.0, first",
            "-0.0001, first",
            "0.0, first",
            "0.0001, second",
            "1.0, second",
            "10.0, second",
            "100.0, second"
    })
    @DisplayName("проверка выбора ветки и отсутствия исключений")
    void testSystem_BranchSelection(double x, String expectedBranch) {
        FunctionSystem system = new FunctionSystem(EPSILON, MAX_ITERATIONS);

        assertDoesNotThrow(() -> system.solve(x));

        if ("second".equals(expectedBranch)) {
            double result = system.solve(x);
            if (x >= 1.0) {
                assertFalse(Double.isNaN(result),
                        "для x >= 1.0 вторая ветка не должна возвращать NaN");
            }
        }
    }
    @Test
    @DisplayName("проверка доступности всех подфункций")
    void testSystem_SubFunctions() {
        FunctionSystem system = new FunctionSystem(EPSILON, MAX_ITERATIONS);
        assertNotNull(system);
        assertDoesNotThrow(() -> system.solve(-Math.PI / 4.0));
        assertDoesNotThrow(() -> system.solve(2.0));
    }
}