package parth.appdev.edgeaiassistant

import org.junit.Assert.*
import org.junit.Test
import parth.appdev.edgeaiassistant.util.InputSanitizer

class InputSanitizerTest {

    @Test
    fun `normal input passes through`() {
        val result = InputSanitizer.sanitize("set alarm for 7am")
        assertEquals("set alarm for 7am", result)
    }

    @Test
    fun `trims whitespace`() {
        val result = InputSanitizer.sanitize("  hello world  ")
        assertEquals("hello world", result)
    }

    @Test
    fun `blank input returns null`() {
        assertNull(InputSanitizer.sanitize(""))
        assertNull(InputSanitizer.sanitize("   "))
    }

    @Test
    fun `emoji only returns null`() {
        val result = InputSanitizer.sanitize("😂😂😂")
        assertNull(result)
    }

    @Test
    fun `strips html tags`() {
        val result = InputSanitizer.sanitize("<script>alert('x')</script>hello")
        assertTrue(result?.contains("<script>") == false)
    }

    @Test
    fun `caps at 200 characters`() {
        val long   = "a".repeat(300)
        val result = InputSanitizer.sanitize(long)
        assertNotNull(result)
        assertTrue(result!!.length <= 200)
    }

    @Test
    fun `collapses multiple spaces`() {
        val result = InputSanitizer.sanitize("convert  5   km  to  miles")
        assertFalse(result?.contains("  ") == true)
    }
}