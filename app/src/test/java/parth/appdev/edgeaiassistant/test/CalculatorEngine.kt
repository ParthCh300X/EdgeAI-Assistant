package parth.appdev.edgeaiassistant

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import parth.appdev.edgeaiassistant.features.calculator.CalculatorEngine

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setup() {
        engine = CalculatorEngine()
    }

    @Test
    fun `addition works`() {
        assertEquals(8.0, engine.evaluate("5+3"), 0.001)
    }

    @Test
    fun `subtraction works`() {
        assertEquals(7.0, engine.evaluate("10-3"), 0.001)
    }

    @Test
    fun `multiplication works`() {
        assertEquals(20.0, engine.evaluate("4*5"), 0.001)
    }

    @Test
    fun `division works`() {
        assertEquals(5.0, engine.evaluate("10/2"), 0.001)
    }

    @Test
    fun `operator precedence works`() {
        // 2 + 3 * 4 = 14 not 20
        assertEquals(14.0, engine.evaluate("2+3*4"), 0.001)
    }

    @Test
    fun `parentheses override precedence`() {
        assertEquals(20.0, engine.evaluate("(2+3)*4"), 0.001)
    }

    @Test
    fun `decimal numbers work`() {
        assertEquals(6.6, engine.evaluate("3.3*2"), 0.001)
    }

    @Test
    fun `chained operations work`() {
        assertEquals(10.0, engine.evaluate("2+3+5"), 0.001)
    }

    @Test(expected = ArithmeticException::class)
    fun `division by zero throws`() {
        engine.evaluate("5/0")
    }

    @Test
    fun `large numbers work`() {
        assertEquals(1_000_000.0, engine.evaluate("1000*1000"), 0.001)
    }
}