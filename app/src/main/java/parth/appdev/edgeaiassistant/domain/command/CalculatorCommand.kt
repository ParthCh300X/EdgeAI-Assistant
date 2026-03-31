package parth.appdev.edgeaiassistant.domain.command

import parth.appdev.edgeaiassistant.features.calculator.CalculatorEngine

class CalculatorCommand(
    private val input: String
) : Command {

    override fun execute(): String {
        return try {
            val normalized = normalizeExpression(input)

            if (normalized.isBlank()) {
                return "Invalid expression"
            }

            val result = CalculatorEngine().evaluate(normalized)

            "Result: $result"
        } catch (e: Exception) {
            "Invalid expression"
        }
    }

    private fun normalizeExpression(input: String): String {

        var text = input.lowercase()

        // 🔥 STRUCTURED PATTERNS
        Regex("sum of (\\d+) and (\\d+)").find(text)?.let {
            val (a, b) = it.destructured
            return "$a+$b"
        }

        Regex("add (\\d+) and (\\d+)").find(text)?.let {
            val (a, b) = it.destructured
            return "$a+$b"
        }

        Regex("product of (\\d+) and (\\d+)").find(text)?.let {
            val (a, b) = it.destructured
            return "$a*$b"
        }

        Regex("difference of (\\d+) and (\\d+)").find(text)?.let {
            val (a, b) = it.destructured
            return "$a-$b"
        }

        Regex("quotient of (\\d+) and (\\d+)").find(text)?.let {
            val (a, b) = it.destructured
            return "$a/$b"
        }

        Regex("subtract (\\d+) from (\\d+)").find(text)?.let {
            val (a, b) = it.destructured
            return "$b-$a"
        }

        Regex("divide (\\d+) by (\\d+)").find(text)?.let {
            val (a, b) = it.destructured
            return "$a/$b"
        }

        Regex("multiply (\\d+) by (\\d+)").find(text)?.let {
            val (a, b) = it.destructured
            return "$a*$b"
        }

        // 🔥 PHRASES
        text = text
            .replace("multiplied by", "*")
            .replace("divided by", "/")
            .replace("added to", "+")
            .replace("subtracted from", "-")

        // 🔥 WORDS
        text = text
            .replace("plus", "+")
            .replace("add", "+")
            .replace("and", "+")
            .replace("sum", "+")

            .replace("minus", "-")
            .replace("subtract", "-")
            .replace("less", "-")

            .replace("times", "*")
            .replace("multiply", "*")
            .replace("multiplied", "*")
            .replace("x", "*")

            .replace("divide", "/")
            .replace("divided", "/")
            .replace("over", "/")

        // 🔥 REMOVE FILLERS
        text = text
            .replace("what is", "")
            .replace("calculate", "")
            .replace("find", "")
            .replace("compute", "")
            .replace("evaluate", "")

        // 🔥 CLEAN
        text = text.replace(Regex("[^0-9+\\-*/().]"), "")

        // 🔥 FIX DOUBLE OPERATORS
        text = text.replace(Regex("\\++"), "+")
        text = text.replace(Regex("--+"), "-")

        return text
    }
}