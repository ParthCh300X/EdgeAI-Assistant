package parth.appdev.edgeaiassistant.domain.command

import parth.appdev.edgeaiassistant.features.converter.UnitConverterEngine

class UnitConvertCommand(
    private val input: String
) : Command {

    override fun execute(): String {

        val parsed = parseInput(input) ?: return "Invalid conversion"

        val result = UnitConverterEngine.convert(
            value = parsed.first,
            from = parsed.second,
            to = parsed.third
        ) ?: return "Conversion not supported"

        return "${parsed.first} ${parsed.second} = $result ${parsed.third}"
    }

    private fun parseInput(input: String): Triple<Double, String, String>? {

        val text = input.lowercase()

        // match: "convert 5 km to meter"
        val regex = Regex("(\\d+(\\.\\d+)?)\\s*(\\w+)\\s*(to|in)?\\s*(\\w+)")
        val match = regex.find(text) ?: return null

        val value = match.groupValues[1].toDoubleOrNull() ?: return null
        val from = normalizeUnit(match.groupValues[3])
        val to = normalizeUnit(match.groupValues[5])

        return Triple(value, from, to)
    }

    private fun normalizeUnit(unit: String): String {
        return when (unit) {
            "km", "kilometer", "kilometers" -> "km"
            "m", "meter", "meters" -> "m"
            "cm", "centimeter", "centimeters" -> "cm"

            "kg", "kilogram", "kilograms" -> "kg"
            "g", "gram", "grams" -> "g"

            "c", "celsius" -> "c"
            "f", "fahrenheit" -> "f"

            else -> unit
        }
    }
}