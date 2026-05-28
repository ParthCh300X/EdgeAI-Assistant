package parth.appdev.edgeaiassistant.domain.command

import parth.appdev.edgeaiassistant.features.converter.UnitConverterEngine

class UnitConvertCommand(private val input: String) : Command {

    // Currency codes — detected by length and uppercase pattern
    private val currencyCodes = setOf(
        "usd","eur","gbp","inr","jpy","cad","aud","chf","cny","krw",
        "sgd","aed","mxn","brl","rub","zar","nok","sek","dkk","nzd"
    )

    override suspend fun execute(): String {
        val parsed = parseInput(input) ?: return "Invalid conversion. Try: convert 5 km to miles"

        val (value, from, to) = parsed

        // Currency path
        if (from in currencyCodes || to in currencyCodes) {
            return UnitConverterEngine.convertCurrency(value, from, to)
        }

        // Unit path
        val result = UnitConverterEngine.convert(value, from, to)
            ?: return "Conversion not supported: $from → $to"

        val formatted = if (result == result.toLong().toDouble()) result.toLong().toString()
        else "%.4f".format(result).trimEnd('0').trimEnd('.')

        return "$value $from = $formatted $to"
    }

    private fun parseInput(input: String): Triple<Double, String, String>? {
        val text = input.lowercase()
        val regex = Regex("(\\d+(\\.\\d+)?)\\s*(\\w+)\\s*(to|in|into)?\\s*(\\w+)")
        val match = regex.find(text) ?: return null

        val value = match.groupValues[1].toDoubleOrNull() ?: return null
        val from  = normalizeUnit(match.groupValues[3])
        val to    = normalizeUnit(match.groupValues[5])

        return Triple(value, from, to)
    }

    private fun normalizeUnit(u: String): String = when (u.lowercase()) {
        "km", "kilometer", "kilometers", "kilometre", "kilometres" -> "km"
        "m", "meter", "meters", "metre", "metres"                  -> "m"
        "cm", "centimeter", "centimeters"                          -> "cm"
        "mm", "millimeter", "millimeters"                          -> "mm"
        "mile", "miles"                                            -> "miles"
        "foot", "feet", "ft"                                       -> "feet"
        "inch", "inches", "in"                                     -> "inches"
        "yard", "yards"                                            -> "yards"
        "kg", "kilogram", "kilograms"                              -> "kg"
        "g", "gram", "grams"                                       -> "g"
        "mg", "milligram", "milligrams"                            -> "mg"
        "lb", "lbs", "pound", "pounds"                             -> "lb"
        "oz", "ounce", "ounces"                                    -> "oz"
        "c", "celsius"                                             -> "c"
        "f", "fahrenheit"                                          -> "f"
        "k", "kelvin"                                              -> "k"
        "l", "liter", "litre", "liters", "litres"                  -> "l"
        "ml", "milliliter", "millilitre"                           -> "ml"
        "gallon", "gallons"                                        -> "gallon"
        "kmph", "kph"                                              -> "kmph"
        "mph"                                                      -> "mph"
        "ms", "m/s"                                                -> "ms"
        "gb", "gigabyte", "gigabytes"                              -> "gb"
        "mb", "megabyte", "megabytes"                              -> "mb"
        "kb", "kilobyte", "kilobytes"                              -> "kb"
        "tb", "terabyte", "terabytes"                              -> "tb"
        "hour", "hours", "hr", "hrs"                               -> "hours"
        "minute", "minutes", "min", "mins"                         -> "minutes"
        "second", "seconds", "sec", "secs"                         -> "seconds"
        "day", "days"                                              -> "days"
        else                                                       -> u.lowercase()
    }
}