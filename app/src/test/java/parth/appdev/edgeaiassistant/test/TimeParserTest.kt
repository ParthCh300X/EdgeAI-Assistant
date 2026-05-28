package parth.appdev.edgeaiassistant

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import parth.appdev.edgeaiassistant.engine.slots.TimeParser
import java.util.Calendar

class TimeParserTest {

    private lateinit var parser: TimeParser

    @Before
    fun setup() {
        parser = TimeParser()
    }

    @Test
    fun `parses 7am`() {
        val result = parser.parseTime("set alarm for 7am")
        assertNotNull(result)
        val cal = Calendar.getInstance().apply { timeInMillis = result!! }
        assertEquals(7, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `parses 9pm`() {
        val result = parser.parseTime("alarm at 9pm")
        assertNotNull(result)
        val cal = Calendar.getInstance().apply { timeInMillis = result!! }
        assertEquals(21, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `parses noon`() {
        val result = parser.parseTime("alarm at noon")
        assertNotNull(result)
        val cal = Calendar.getInstance().apply { timeInMillis = result!! }
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `parses midnight`() {
        val result = parser.parseTime("set alarm for midnight")
        assertNotNull(result)
        val cal = Calendar.getInstance().apply { timeInMillis = result!! }
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `parses in X minutes`() {
        val before = System.currentTimeMillis()
        val result = parser.parseTime("in 30 minutes")
        assertNotNull(result)
        val diff = result!! - before
        assertTrue("Should be ~30 minutes ahead", diff in 29 * 60_000L..31 * 60_000L)
    }

    @Test
    fun `parses in X hours`() {
        val before = System.currentTimeMillis()
        val result = parser.parseTime("in 2 hours")
        assertNotNull(result)
        val diff = result!! - before
        assertTrue("Should be ~2 hours ahead", diff in 119 * 60_000L..121 * 60_000L)
    }

    @Test
    fun `parses half past 3`() {
        val result = parser.parseTime("half past 3")
        assertNotNull(result)
        val cal = Calendar.getInstance().apply { timeInMillis = result!! }
        assertEquals(30, cal.get(Calendar.MINUTE))
    }

    @Test
    fun `parses quarter to 9`() {
        val result = parser.parseTime("quarter to 9")
        assertNotNull(result)
        val cal = Calendar.getInstance().apply { timeInMillis = result!! }
        assertEquals(45, cal.get(Calendar.MINUTE))
    }

    @Test
    fun `parses HH MM format`() {
        val result = parser.parseTime("alarm at 7:30am")
        assertNotNull(result)
        val cal = Calendar.getInstance().apply { timeInMillis = result!! }
        assertEquals(7, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, cal.get(Calendar.MINUTE))
    }

    @Test
    fun `always returns future time`() {
        // Even for a past time today, result must be in future
        val result = parser.parseTime("alarm 1am")
        assertNotNull(result)
        assertTrue("Time must be in future", result!! > System.currentTimeMillis())
    }

    @Test
    fun `returns null for garbage input`() {
        val result = parser.parseTime("buy groceries")
        assertNull(result)
    }
}