package parth.appdev.edgeaiassistant

import org.junit.Assert.*
import org.junit.Test
import parth.appdev.edgeaiassistant.features.converter.UnitConverterEngine

class UnitConverterEngineTest {

    @Test
    fun `km to miles`() {
        val result = UnitConverterEngine.convert(1.0, "km", "miles")
        assertEquals(0.621371, result!!, 0.001)
    }

    @Test
    fun `miles to km`() {
        val result = UnitConverterEngine.convert(1.0, "miles", "km")
        assertEquals(1.60934, result!!, 0.001)
    }

    @Test
    fun `kg to g`() {
        val result = UnitConverterEngine.convert(1.0, "kg", "g")
        assertEquals(1000.0, result!!, 0.001)
    }

    @Test
    fun `celsius to fahrenheit`() {
        val result = UnitConverterEngine.convert(100.0, "c", "f")
        assertEquals(212.0, result!!, 0.001)
    }

    @Test
    fun `fahrenheit to celsius`() {
        val result = UnitConverterEngine.convert(32.0, "f", "c")
        assertEquals(0.0, result!!, 0.001)
    }

    @Test
    fun `km to m`() {
        val result = UnitConverterEngine.convert(1.0, "km", "m")
        assertEquals(1000.0, result!!, 0.001)
    }

    @Test
    fun `gb to mb`() {
        val result = UnitConverterEngine.convert(1.0, "gb", "mb")
        assertEquals(1024.0, result!!, 0.001)
    }

    @Test
    fun `kmph to mph`() {
        val result = UnitConverterEngine.convert(100.0, "kmph", "mph")
        assertEquals(62.1371, result!!, 0.001)
    }

    @Test
    fun `same unit returns same value`() {
        val result = UnitConverterEngine.convert(5.0, "km", "km")
        assertEquals(5.0, result!!, 0.001)
    }

    @Test
    fun `unsupported conversion returns null`() {
        val result = UnitConverterEngine.convert(1.0, "parsec", "furlong")
        assertNull(result)
    }

    @Test
    fun `litre to ml`() {
        val result = UnitConverterEngine.convert(2.0, "l", "ml")
        assertEquals(2000.0, result!!, 0.001)
    }

    @Test
    fun `feet to inches`() {
        val result = UnitConverterEngine.convert(1.0, "feet", "inches")
        assertEquals(12.0, result!!, 0.001)
    }
}