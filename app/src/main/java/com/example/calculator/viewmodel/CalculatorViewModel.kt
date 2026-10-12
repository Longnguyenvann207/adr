package com.example.calculator.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calculator.engine.ExpressionEvaluator
import com.example.calculator.model.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String? = null,
    val finalResult: String? = null,
    val isResultDisplayed: Boolean = false,
    val isDegreeMode: Boolean = true,
    val isScientificExpanded: Boolean = false,
    val memoryValue: Double = 0.0,
    val history: List<HistoryItem> = emptyList(),
    val isHistoryVisible: Boolean = false,
    val errorMessage: String? = null
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onDigit(digit: String) {
        _uiState.update { current ->
            val newExpr = if (current.isResultDisplayed) {
                // If previous result was displayed, starting a new digit replaces it
                digit
            } else {
                if (current.expression == "0") digit else current.expression + digit
            }
            val preview = ExpressionEvaluator.evaluatePreview(newExpr, current.isDegreeMode)
            current.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    fun onDecimal() {
        _uiState.update { current ->
            val expr = if (current.isResultDisplayed) "0." else current.expression
            val lastToken = getLastNumberToken(expr)

            val updatedExpr = when {
                current.isResultDisplayed -> "0."
                expr.isEmpty() || isLastCharOperator(expr) || expr.endsWith("(") -> "${expr}0."
                lastToken.contains(".") -> expr // Already contains decimal in this number token
                else -> "$expr."
            }

            val preview = ExpressionEvaluator.evaluatePreview(updatedExpr, current.isDegreeMode)
            current.copy(
                expression = updatedExpr,
                previewResult = preview,
                finalResult = null,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { current ->
            val baseExpr = if (current.isResultDisplayed && current.finalResult != null) {
                // Continuing from previous result
                cleanFormattedNumber(current.finalResult)
            } else {
                current.expression
            }

            val updatedExpr = when {
                baseExpr.isEmpty() -> {
                    if (op == "−" || op == "-") "−" else ""
                }
                isLastCharOperator(baseExpr) -> {
                    // Replace trailing operator with the new one
                    baseExpr.dropLast(1) + op
                }
                baseExpr.endsWith("(") -> {
                    if (op == "−" || op == "-") baseExpr + "−" else baseExpr
                }
                else -> baseExpr + op
            }

            val preview = ExpressionEvaluator.evaluatePreview(updatedExpr, current.isDegreeMode)
            current.copy(
                expression = updatedExpr,
                previewResult = preview,
                finalResult = null,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    fun onParenthesis() {
        _uiState.update { current ->
            val expr = if (current.isResultDisplayed) "" else current.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val updatedExpr = if (expr.isEmpty() || isLastCharOperator(expr) || expr.endsWith("(")) {
                "$expr("
            } else if (openCount > closeCount) {
                "$expr)"
            } else {
                "$expr×("
            }

            val preview = ExpressionEvaluator.evaluatePreview(updatedExpr, current.isDegreeMode)
            current.copy(
                expression = updatedExpr,
                previewResult = preview,
                finalResult = null,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    fun onClear() {
        _uiState.update { current ->
            current.copy(
                expression = "",
                previewResult = null,
                finalResult = null,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    fun onBackspace() {
        _uiState.update { current ->
            if (current.isResultDisplayed) {
                current.copy(
                    expression = "",
                    previewResult = null,
                    finalResult = null,
                    isResultDisplayed = false,
                    errorMessage = null
                )
            } else if (current.expression.isNotEmpty()) {
                val expr = current.expression
                val newExpr = if (expr.endsWith("sin(") || expr.endsWith("cos(") ||
                    expr.endsWith("tan(") || expr.endsWith("log(")
                ) {
                    expr.dropLast(4)
                } else if (expr.endsWith("ln(") || expr.endsWith("√(")) {
                    expr.dropLast(3)
                } else {
                    expr.dropLast(1)
                }
                val preview = ExpressionEvaluator.evaluatePreview(newExpr, current.isDegreeMode)
                current.copy(
                    expression = newExpr,
                    previewResult = preview,
                    errorMessage = null
                )
            } else {
                current
            }
        }
    }

    fun onToggleSign() {
        _uiState.update { current ->
            val expr = if (current.isResultDisplayed && current.finalResult != null) {
                cleanFormattedNumber(current.finalResult)
            } else {
                current.expression
            }

            if (expr.isEmpty()) return@update current

            // Find last number or token
            val lastToken = getLastNumberToken(expr)
            if (lastToken.isEmpty()) return@update current

            val prefix = expr.dropLast(lastToken.length)
            val updatedExpr = if (prefix.endsWith("−")) {
                prefix.dropLast(1) + lastToken
            } else if (prefix.endsWith("-")) {
                prefix.dropLast(1) + lastToken
            } else if (prefix.isEmpty()) {
                "−$lastToken"
            } else {
                "$prefix(−$lastToken)"
            }

            val preview = ExpressionEvaluator.evaluatePreview(updatedExpr, current.isDegreeMode)
            current.copy(
                expression = updatedExpr,
                previewResult = preview,
                finalResult = null,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    fun onPercent() {
        _uiState.update { current ->
            val expr = if (current.isResultDisplayed && current.finalResult != null) {
                cleanFormattedNumber(current.finalResult)
            } else {
                current.expression
            }

            if (expr.isEmpty() || isLastCharOperator(expr)) return@update current

            val updatedExpr = "$expr%"
            val preview = ExpressionEvaluator.evaluatePreview(updatedExpr, current.isDegreeMode)
            current.copy(
                expression = updatedExpr,
                previewResult = preview,
                finalResult = null,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    fun onFunction(func: String) {
        _uiState.update { current ->
            val baseExpr = if (current.isResultDisplayed && current.finalResult != null) {
                cleanFormattedNumber(current.finalResult)
            } else {
                current.expression
            }

            val updatedExpr = when (func) {
                "sqrt" -> if (baseExpr.isEmpty() || isLastCharOperator(baseExpr)) "${baseExpr}√(" else "$baseExpr×√("
                "sqr" -> if (baseExpr.isEmpty()) "" else "$baseExpr^2"
                "pow" -> if (baseExpr.isEmpty() || isLastCharOperator(baseExpr)) baseExpr else "$baseExpr^"
                "inv" -> if (baseExpr.isEmpty() || isLastCharOperator(baseExpr)) "${baseExpr}1/(" else "$baseExpr×(1/"
                "fact" -> if (baseExpr.isEmpty() || isLastCharOperator(baseExpr)) baseExpr else "$baseExpr!"
                "sin", "cos", "tan", "ln", "log" -> {
                    if (baseExpr.isEmpty() || isLastCharOperator(baseExpr) || baseExpr.endsWith("(")) {
                        "$baseExpr$func("
                    } else {
                        "$baseExpr×$func("
                    }
                }
                else -> baseExpr
            }

            val preview = ExpressionEvaluator.evaluatePreview(updatedExpr, current.isDegreeMode)
            current.copy(
                expression = updatedExpr,
                previewResult = preview,
                finalResult = null,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    fun onConstant(constant: String) {
        _uiState.update { current ->
            val expr = if (current.isResultDisplayed) "" else current.expression
            val updatedExpr = if (expr.isEmpty() || isLastCharOperator(expr) || expr.endsWith("(")) {
                "$expr$constant"
            } else {
                "$expr×$constant"
            }
            val preview = ExpressionEvaluator.evaluatePreview(updatedExpr, current.isDegreeMode)
            current.copy(
                expression = updatedExpr,
                previewResult = preview,
                finalResult = null,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    fun onEquals() {
        val current = _uiState.value
        val expr = current.expression.trim()
        if (expr.isEmpty()) return

        when (val eval = ExpressionEvaluator.evaluate(expr, current.isDegreeMode)) {
            is ExpressionEvaluator.EvaluationResult.Success -> {
                val newHistory = listOf(
                    HistoryItem(expression = expr, result = eval.formatted)
                ) + current.history

                _uiState.update {
                    it.copy(
                        finalResult = eval.formatted,
                        previewResult = null,
                        isResultDisplayed = true,
                        history = newHistory.take(50),
                        errorMessage = null
                    )
                }
            }
            is ExpressionEvaluator.EvaluationResult.Error -> {
                _uiState.update {
                    it.copy(
                        errorMessage = eval.message,
                        previewResult = null,
                        finalResult = null
                    )
                }
            }
        }
    }

    fun onToggleAngleMode() {
        _uiState.update { current ->
            val newDegreeMode = !current.isDegreeMode
            val preview = ExpressionEvaluator.evaluatePreview(current.expression, newDegreeMode)
            current.copy(
                isDegreeMode = newDegreeMode,
                previewResult = preview
            )
        }
    }

    fun onToggleScientific() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun onToggleHistory(visible: Boolean? = null) {
        _uiState.update { current ->
            current.copy(isHistoryVisible = visible ?: !current.isHistoryVisible)
        }
    }

    fun onClearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun onSelectHistoryItem(item: HistoryItem, useResultOnly: Boolean = false) {
        _uiState.update { current ->
            if (useResultOnly) {
                val cleanRes = cleanFormattedNumber(item.result)
                current.copy(
                    expression = cleanRes,
                    finalResult = item.result,
                    isResultDisplayed = true,
                    previewResult = null,
                    isHistoryVisible = false,
                    errorMessage = null
                )
            } else {
                current.copy(
                    expression = item.expression,
                    finalResult = item.result,
                    isResultDisplayed = true,
                    previewResult = null,
                    isHistoryVisible = false,
                    errorMessage = null
                )
            }
        }
    }

    fun onMemoryAdd() {
        val current = _uiState.value
        val valueToAdd = getCurrentValueDouble(current)
        _uiState.update { it.copy(memoryValue = it.memoryValue + valueToAdd) }
    }

    fun onMemorySubtract() {
        val current = _uiState.value
        val valueToSub = getCurrentValueDouble(current)
        _uiState.update { it.copy(memoryValue = it.memoryValue - valueToSub) }
    }

    fun onMemoryRecall() {
        val current = _uiState.value
        val memFormatted = ExpressionEvaluator.formatResult(current.memoryValue)
        val clean = cleanFormattedNumber(memFormatted)
        val newExpr = if (current.expression.isEmpty() || isLastCharOperator(current.expression)) {
            current.expression + clean
        } else {
            current.expression + "×" + clean
        }
        val preview = ExpressionEvaluator.evaluatePreview(newExpr, current.isDegreeMode)
        _uiState.update {
            it.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                isResultDisplayed = false
            )
        }
    }

    fun onMemoryClear() {
        _uiState.update { it.copy(memoryValue = 0.0) }
    }

    private fun getCurrentValueDouble(state: CalculatorUiState): Double {
        if (state.finalResult != null) {
            val clean = cleanFormattedNumber(state.finalResult)
            return clean.toDoubleOrNull() ?: 0.0
        }
        if (state.expression.isNotEmpty()) {
            return when (val eval = ExpressionEvaluator.evaluate(state.expression, state.isDegreeMode)) {
                is ExpressionEvaluator.EvaluationResult.Success -> eval.value
                else -> 0.0
            }
        }
        return 0.0
    }

    private fun cleanFormattedNumber(formatted: String): String {
        return formatted.replace(",", "")
    }

    private fun isLastCharOperator(expr: String): Boolean {
        if (expr.isEmpty()) return false
        val last = expr.last()
        return last in listOf('+', '−', '-', '×', '*', '÷', '/', '^')
    }

    private fun getLastNumberToken(expr: String): String {
        var idx = expr.length - 1
        while (idx >= 0 && (expr[idx].isDigit() || expr[idx] == '.')) {
            idx--
        }
        return expr.substring(idx + 1)
    }
}
