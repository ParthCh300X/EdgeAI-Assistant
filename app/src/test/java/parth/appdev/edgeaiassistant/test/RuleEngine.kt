package parth.appdev.edgeaiassistant

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import parth.appdev.edgeaiassistant.domain.intent.IntentType
import parth.appdev.edgeaiassistant.engine.rules.RuleEngine

class RuleEngineTest {

    private lateinit var engine: RuleEngine

    @Before
    fun setup() {
        engine = RuleEngine()
    }

    @Test
    fun `detects calculator intent from expression`() {
        val result = engine.detectIntent("5+3")
        assertEquals(IntentType.CALCULATE, result.intent)
    }

    @Test
    fun `detects calculator from keyword`() {
        val result = engine.detectIntent("calculate 10 times 5")
        assertEquals(IntentType.CALCULATE, result.intent)
    }

    @Test
    fun `detects unit converter from keyword`() {
        val result = engine.detectIntent("convert 5 km to miles")
        assertEquals(IntentType.CONVERT_UNITS, result.intent)
    }

    @Test
    fun `detects unit converter from unit in sentence`() {
        val result = engine.detectIntent("10 kg in pounds")
        assertEquals(IntentType.CONVERT_UNITS, result.intent)
    }

    @Test
    fun `detects get notes intent`() {
        val result = engine.detectIntent("show my notes")
        assertEquals(IntentType.GET_NOTES, result.intent)
    }

    @Test
    fun `detects take note intent`() {
        val result = engine.detectIntent("note buy groceries")
        assertEquals(IntentType.TAKE_NOTE, result.intent)
    }

    @Test
    fun `detects alarm intent`() {
        val result = engine.detectIntent("set alarm for 7am")
        assertEquals(IntentType.SET_ALARM, result.intent)
    }

    @Test
    fun `detects timer intent`() {
        val result = engine.detectIntent("set a timer for 5 minutes")
        assertEquals(IntentType.TIMER, result.intent)
    }

    @Test
    fun `detects open app intent`() {
        val result = engine.detectIntent("open youtube")
        assertEquals(IntentType.OPEN_APP, result.intent)
    }

    @Test
    fun `detects weather intent`() {
        val result = engine.detectIntent("what is the weather")
        assertEquals(IntentType.WEATHER, result.intent)
    }

    @Test
    fun `returns GENERAL for unknown input`() {
        val result = engine.detectIntent("hello how are you")
        assertEquals(IntentType.GENERAL, result.intent)
    }

    @Test
    fun `confidence is above 0 for all non-general intents`() {
        val inputs = listOf(
            "calculate 5+3",
            "convert 10 km to miles",
            "note buy milk",
            "show my notes",
            "set alarm 7am",
            "open youtube",
            "weather today"
        )
        inputs.forEach { input ->
            val result = engine.detectIntent(input)
            assertTrue(
                "Expected non-zero confidence for: $input",
                result.confidence > 0f
            )
        }
    }
}