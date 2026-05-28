package parth.appdev.edgeaiassistant.features.converter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object UnitConverterEngine {

    // Simple 1-hour memory cache for exchange rates
    private var ratesCache: Map<String, Double>? = null
    private var ratesCacheTime: Long = 0L
    private val client = OkHttpClient()

    fun convert(value: Double, from: String, to: String): Double? {
        if (from == to) return value
        return when {
            // LENGTH
            from == "km"    && to == "m"     -> value * 1000
            from == "m"     && to == "km"    -> value / 1000
            from == "m"     && to == "cm"    -> value * 100
            from == "cm"    && to == "m"     -> value / 100
            from == "km"    && to == "miles" -> value * 0.621371
            from == "miles" && to == "km"    -> value / 0.621371
            from == "m"     && to == "feet"  -> value * 3.28084
            from == "feet"  && to == "m"     -> value / 3.28084
            from == "m"     && to == "inches"-> value * 39.3701
            from == "inches"&& to == "m"     -> value / 39.3701
            from == "cm"    && to == "inches"-> value / 2.54
            from == "inches"&& to == "cm"    -> value * 2.54
            from == "feet"  && to == "inches"-> value * 12
            from == "inches"&& to == "feet"  -> value / 12
            from == "yards" && to == "m"     -> value * 0.9144
            from == "m"     && to == "yards" -> value / 0.9144

            // MASS
            from == "kg"  && to == "g"       -> value * 1000
            from == "g"   && to == "kg"      -> value / 1000
            from == "kg"  && to == "lb"      -> value * 2.20462
            from == "lb"  && to == "kg"      -> value / 2.20462
            from == "g"   && to == "oz"      -> value * 0.035274
            from == "oz"  && to == "g"       -> value / 0.035274
            from == "lb"  && to == "oz"      -> value * 16
            from == "oz"  && to == "lb"      -> value / 16
            from == "kg"  && to == "mg"      -> value * 1_000_000
            from == "mg"  && to == "kg"      -> value / 1_000_000

            // TEMPERATURE
            from == "c" && to == "f"         -> (value * 9 / 5) + 32
            from == "f" && to == "c"         -> (value - 32) * 5 / 9
            from == "c" && to == "k"         -> value + 273.15
            from == "k" && to == "c"         -> value - 273.15

            // VOLUME
            from == "l"   && to == "ml"      -> value * 1000
            from == "ml"  && to == "l"       -> value / 1000
            from == "l"   && to == "gallon"  -> value * 0.264172
            from == "gallon"&& to == "l"     -> value / 0.264172

            // SPEED
            from == "kmph" && to == "mph"    -> value * 0.621371
            from == "mph"  && to == "kmph"   -> value / 0.621371
            from == "ms"   && to == "kmph"   -> value * 3.6
            from == "kmph" && to == "ms"     -> value / 3.6

            // DATA
            from == "gb"  && to == "mb"      -> value * 1024
            from == "mb"  && to == "gb"      -> value / 1024
            from == "mb"  && to == "kb"      -> value * 1024
            from == "kb"  && to == "mb"      -> value / 1024
            from == "gb"  && to == "tb"      -> value / 1024
            from == "tb"  && to == "gb"      -> value * 1024
            from == "kb"  && to == "bytes"   -> value * 1024
            from == "bytes"&& to == "kb"     -> value / 1024

            // TIME
            from == "hours"   && to == "minutes" -> value * 60
            from == "minutes" && to == "hours"   -> value / 60
            from == "minutes" && to == "seconds" -> value * 60
            from == "seconds" && to == "minutes" -> value / 60
            from == "hours"   && to == "seconds" -> value * 3600
            from == "seconds" && to == "hours"   -> value / 3600
            from == "days"    && to == "hours"   -> value * 24
            from == "hours"   && to == "days"    -> value / 24

            else -> null
        }
    }

    // Currency — called as suspend from coroutine
    suspend fun convertCurrency(amount: Double, from: String, to: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val rates = getCachedRates(from)
                    ?: return@withContext "Couldn't fetch exchange rates. Check internet."

                val rate = rates[to.uppercase()]
                    ?: return@withContext "Currency '$to' not supported."

                val result = amount * rate
                val formatted = "%.2f".format(result)
                "$amount ${from.uppercase()} = $formatted ${to.uppercase()}"
            } catch (e: Exception) {
                "Currency conversion failed. Check internet."
            }
        }
    }

    private fun getCachedRates(base: String): Map<String, Double>? {
        val now = System.currentTimeMillis()
        if (ratesCache != null && (now - ratesCacheTime) < 3600_000L) return ratesCache

        return try {
            val url  = "https://open.er-api.com/v6/latest/${base.uppercase()}"
            val req  = Request.Builder().url(url).build()
            val resp = client.newCall(req).execute()
            val body = resp.body?.string() ?: return null
            val json = JSONObject(body)
            if (json.getString("result") != "success") return null

            val ratesJson = json.getJSONObject("rates")
            val map = mutableMapOf<String, Double>()
            ratesJson.keys().forEach { key -> map[key] = ratesJson.getDouble(key) }

            ratesCache     = map
            ratesCacheTime = now
            map
        } catch (e: Exception) {
            null
        }
    }
}