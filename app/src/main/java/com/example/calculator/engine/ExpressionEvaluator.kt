package com.example.calculator.engine

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.PI
import kotlin.math.E
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object ExpressionEvaluator {

    sealed class EvaluationResult {
        data class Success(val value: Double, val formatted: String) : EvaluationResult()
        data class Error(val message: String) : EvaluationResult()
    }

    /**
     * Evaluates a mathematical expression string.
     * @param rawExpr The raw input expression.
     * @param isDegrees Whether trigonometric calculations should use degrees (true) or radians (false).
     */
    fun evaluate(rawExpr: String, isDegrees: Boolean = true): EvaluationResult {
        if (rawExpr.isBlank()) {
            return EvaluationResult.Success(0.0, "0")
        }

        return try {
            val sanitized = sanitizeExpression(rawExpr)
            if (sanitized.isBlank()) {
                return EvaluationResult.Success(0.0, "0")
            }

            val tokens = tokenize(sanitized)
            val parser = Parser(tokens, isDegrees)
            val result = parser.parseExpression()

            if (result.isNaN()) {
                EvaluationResult.Error("Undefined")
            } else if (result.isInfinite()) {
                EvaluationResult.Error("Cannot divide by 0")
            } else {
                val formatted = formatResult(result)
                EvaluationResult.Success(result, formatted)
            }
        } catch (e: ArithmeticException) {
            EvaluationResult.Error(e.message ?: "Calculation error")
        } catch (e: Exception) {
            EvaluationResult.Error("Invalid format")
        }
    }

    /**
     * Attempts to evaluate for live preview without failing on trailing operators or unclosed parentheses.
     */
    fun evaluatePreview(rawExpr: String, isDegrees: Boolean = true): String? {
        val trimmed = rawExpr.trim()
        if (trimmed.isEmpty()) return null

        // If it's just a single number, no need for preview
        if (trimmed.toDoubleOrNull() != null) return null

        var sanitized = sanitizeExpression(trimmed)

        // Remove trailing operators for preview
        while (sanitized.isNotEmpty() && (
                sanitized.endsWith("+") || sanitized.endsWith("-") ||
                sanitized.endsWith("*") || sanitized.endsWith("/") ||
                sanitized.endsWith("^") || sanitized.endsWith("(")
            )) {
            sanitized = sanitized.dropLast(1).trim()
        }

        if (sanitized.isEmpty()) return null

        // Balance open parentheses
        val openCount = sanitized.count { it == '(' }
        val closeCount = sanitized.count { it == ')' }
        if (openCount > closeCount) {
            sanitized += ")".repeat(openCount - closeCount)
        }

        return when (val res = evaluate(sanitized, isDegrees)) {
            is EvaluationResult.Success -> res.formatted
            is EvaluationResult.Error -> null
        }
    }

    private fun sanitizeExpression(expr: String): String {
        return expr
            .replace('×', '*')
            .replace('÷', '/')
            .replace('−', '-')
            .replace("√", "sqrt")
            .replace("π", PI.toString())
            .replace("e", E.toString())
            .replace("\\s+".toRegex(), "")
    }

    private enum class TokenType {
        NUMBER, OPERATOR, FUNCTION, LPAREN, RPAREN
    }

    private data class Token(val type: TokenType, val value: String)

    private fun tokenize(expr: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val len = expr.length

        while (i < len) {
            val c = expr[i]

            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < len && (expr[i].isDigit() || expr[i] == '.' || expr[i] == 'E' || expr[i] == 'e')) {
                        // Handle scientific notation e.g. 1e-5
                        if ((expr[i] == 'E' || expr[i] == 'e') && i + 1 < len && (expr[i + 1] == '+' || expr[i + 1] == '-')) {
                            sb.append(expr[i])
                            i++
                            sb.append(expr[i])
                            i++
                        } else {
                            sb.append(expr[i])
                            i++
                        }
                    }
                    tokens.add(Token(TokenType.NUMBER, sb.toString()))
                }
                c == '(' -> {
                    // Check implicit multiplication: e.g. 5(3) or )(
                    if (tokens.isNotEmpty()) {
                        val prev = tokens.last()
                        if (prev.type == TokenType.NUMBER || prev.type == TokenType.RPAREN) {
                            tokens.add(Token(TokenType.OPERATOR, "*"))
                        }
                    }
                    tokens.add(Token(TokenType.LPAREN, "("))
                    i++
                }
                c == ')' -> {
                    tokens.add(Token(TokenType.RPAREN, ")"))
                    i++
                    // Check implicit multiplication after ): e.g. (2)5 or (2)sqrt
                    if (i < len && (expr[i].isDigit() || expr[i] == '(' || expr[i].isLetter())) {
                        tokens.add(Token(TokenType.OPERATOR, "*"))
                    }
                }
                c in "+-*/^%!" -> {
                    tokens.add(Token(TokenType.OPERATOR, c.toString()))
                    i++
                }
                c.isLetter() -> {
                    // Function name or identifiers (sin, cos, tan, ln, log, sqrt)
                    val sb = StringBuilder()
                    while (i < len && expr[i].isLetter()) {
                        sb.append(expr[i])
                        i++
                    }
                    val name = sb.toString().lowercase()
                    // Check implicit multiplication: 5sin(30)
                    if (tokens.isNotEmpty()) {
                        val prev = tokens.last()
                        if (prev.type == TokenType.NUMBER || prev.type == TokenType.RPAREN) {
                            tokens.add(Token(TokenType.OPERATOR, "*"))
                        }
                    }
                    tokens.add(Token(TokenType.FUNCTION, name))
                }
                else -> {
                    i++
                }
            }
        }
        return tokens
    }

    private class Parser(private val tokens: List<Token>, private val isDegrees: Boolean) {
        private var pos = 0

        private fun peek(): Token? = if (pos < tokens.size) tokens[pos] else null
        private fun consume(): Token = tokens[pos++]

        fun parseExpression(): Double {
            val result = parseAdditive()
            if (pos < tokens.size) {
                throw IllegalArgumentException("Unexpected token: ${peek()?.value}")
            }
            return result
        }

        private fun parseAdditive(): Double {
            var left = parseMultiplicative()

            while (pos < tokens.size) {
                val token = peek() ?: break
                if (token.type == TokenType.OPERATOR && (token.value == "+" || token.value == "-")) {
                    consume()
                    val right = parseMultiplicative()
                    left = if (token.value == "+") left + right else left - right
                } else {
                    break
                }
            }
            return left
        }

        private fun parseMultiplicative(): Double {
            var left = parsePower()

            while (pos < tokens.size) {
                val token = peek() ?: break
                if (token.type == TokenType.OPERATOR && (token.value == "*" || token.value == "/" || token.value == "%")) {
                    consume()
                    val right = parsePower()
                    left = when (token.value) {
                        "*" -> left * right
                        "/" -> {
                            if (right == 0.0) throw ArithmeticException("Cannot divide by 0")
                            left / right
                        }
                        "%" -> {
                            if (right == 0.0) throw ArithmeticException("Cannot modulo by 0")
                            left % right
                        }
                        else -> left
                    }
                } else {
                    break
                }
            }
            return left
        }

        private fun parsePower(): Double {
            var left = parseUnary()

            while (pos < tokens.size) {
                val token = peek() ?: break
                if (token.type == TokenType.OPERATOR && token.value == "^") {
                    consume()
                    val right = parseUnary()
                    left = left.pow(right)
                } else if (token.type == TokenType.OPERATOR && token.value == "!") {
                    consume()
                    left = factorial(left)
                } else {
                    break
                }
            }
            return left
        }

        private fun parseUnary(): Double {
            val token = peek() ?: throw IllegalArgumentException("Unexpected end of expression")

            if (token.type == TokenType.OPERATOR) {
                if (token.value == "-") {
                    consume()
                    return -parseUnary()
                } else if (token.value == "+") {
                    consume()
                    return parseUnary()
                }
            }

            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            val token = peek() ?: throw IllegalArgumentException("Unexpected end of expression")

            when (token.type) {
                TokenType.NUMBER -> {
                    consume()
                    var num = token.value.toDoubleOrNull()
                        ?: throw IllegalArgumentException("Invalid number: ${token.value}")
                    // Check if followed by % without second operand e.g. 50%
                    if (pos < tokens.size && peek()?.value == "%") {
                        // Check if it's postfix %
                        val next = tokens.getOrNull(pos + 1)
                        if (next == null || next.type != TokenType.NUMBER) {
                            consume()
                            num /= 100.0
                        }
                    }
                    return num
                }
                TokenType.FUNCTION -> {
                    val fn = consume().value
                    val arg = if (pos < tokens.size && peek()?.type == TokenType.LPAREN) {
                        consume() // consume '('
                        val value = parseAdditive()
                        if (pos < tokens.size && peek()?.type == TokenType.RPAREN) {
                            consume()
                        }
                        value
                    } else {
                        parseUnary()
                    }

                    return when (fn) {
                        "sin" -> {
                            val rad = if (isDegrees) Math.toRadians(arg) else arg
                            val res = sin(rad)
                            if (abs(res) < 1e-15) 0.0 else res
                        }
                        "cos" -> {
                            val rad = if (isDegrees) Math.toRadians(arg) else arg
                            val res = cos(rad)
                            if (abs(res) < 1e-15) 0.0 else res
                        }
                        "tan" -> {
                            val rad = if (isDegrees) Math.toRadians(arg) else arg
                            val c = cos(rad)
                            if (abs(c) < 1e-15) throw ArithmeticException("Undefined tan")
                            tan(rad)
                        }
                        "sqrt" -> {
                            if (arg < 0) throw ArithmeticException("Invalid input (negative sqrt)")
                            sqrt(arg)
                        }
                        "ln" -> {
                            if (arg <= 0) throw ArithmeticException("Invalid input (ln non-positive)")
                            ln(arg)
                        }
                        "log" -> {
                            if (arg <= 0) throw ArithmeticException("Invalid input (log non-positive)")
                            log10(arg)
                        }
                        else -> throw IllegalArgumentException("Unknown function: $fn")
                    }
                }
                TokenType.LPAREN -> {
                    consume()
                    val result = parseAdditive()
                    if (pos < tokens.size && peek()?.type == TokenType.RPAREN) {
                        consume()
                    }
                    return result
                }
                else -> throw IllegalArgumentException("Unexpected token: ${token.value}")
            }
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || n != n.roundToLong().toDouble() || n > 170) {
                throw ArithmeticException("Invalid factorial")
            }
            var fact = 1.0
            val intN = n.roundToLong()
            for (i in 2..intN) {
                fact *= i
            }
            return fact
        }
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Undefined"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        // Round near-zero values
        val cleanValue = if (abs(value) < 1e-12) 0.0 else value

        // If very large or very small, use scientific notation
        val absVal = abs(cleanValue)
        if (absVal >= 1e12 || (absVal > 0.0 && absVal < 1e-7)) {
            val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            return df.format(cleanValue).replace("E", "e")
        }

        // Use BigDecimal for high precision display without 0.30000000000000004
        val bd = try {
            BigDecimal.valueOf(cleanValue).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        } catch (e: Exception) {
            BigDecimal(cleanValue.toString())
        }

        val plain = bd.toPlainString()
        return if (plain.contains('.')) {
            val parts = plain.split('.')
            val intPart = formatIntegerWithCommas(parts[0])
            val decPart = parts[1]
            "$intPart.$decPart"
        } else {
            formatIntegerWithCommas(plain)
        }
    }

    private fun formatIntegerWithCommas(numStr: String): String {
        val isNegative = numStr.startsWith('-')
        val digits = if (isNegative) numStr.substring(1) else numStr
        val formatted = StringBuilder()
        val len = digits.length
        for (i in 0 until len) {
            if (i > 0 && (len - i) % 3 == 0) {
                formatted.append(',')
            }
            formatted.append(digits[i])
        }
        return if (isNegative) "-$formatted" else formatted.toString()
    }
}
