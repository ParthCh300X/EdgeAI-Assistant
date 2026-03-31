package parth.appdev.edgeaiassistant.features.calculator

import java.util.Stack

class CalculatorEngine {

    fun evaluate(expression: String): Double {
        val tokens = tokenize(expression)
        return evaluateTokens(tokens)
    }

    private fun tokenize(expression: String): List<String> {
        val regex = Regex("(?<=[-+*/()])|(?=[-+*/()])")
        return expression.replace(" ", "").split(regex).filter { it.isNotEmpty() }
    }

    private fun evaluateTokens(tokens: List<String>): Double {
        val values = Stack<Double>()
        val ops = Stack<String>()

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> {
                    values.push(token.toDouble())
                }

                token == "(" -> {
                    ops.push(token)
                }

                token == ")" -> {
                    while (ops.isNotEmpty() && ops.peek() != "(") {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                    }
                    if (ops.isNotEmpty()) ops.pop()
                }

                token in listOf("+", "-", "*", "/") -> {
                    while (
                        ops.isNotEmpty() &&
                        ops.peek() != "(" &&
                        precedence(ops.peek()) >= precedence(token)
                    ) {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                    }
                    ops.push(token)
                }
            }
        }

        while (ops.isNotEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> 0
        }
    }

    private fun applyOp(op: String, b: Double, a: Double): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> {
                if (b == 0.0) throw ArithmeticException("Division by zero")
                a / b
            }
            else -> 0.0
        }
    }
}