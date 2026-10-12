package com.example

import com.example.calculator.engine.ExpressionEvaluator
import com.example.calculator.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBasicArithmetic() {
        val res1 = ExpressionEvaluator.evaluate("2 + 3")
        assertTrue(res1 is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("5", (res1 as ExpressionEvaluator.EvaluationResult.Success).formatted)

        val res2 = ExpressionEvaluator.evaluate("10 − 4")
        assertTrue(res2 is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("6", (res2 as ExpressionEvaluator.EvaluationResult.Success).formatted)

        val res3 = ExpressionEvaluator.evaluate("6 × 7")
        assertTrue(res3 is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("42", (res3 as ExpressionEvaluator.EvaluationResult.Success).formatted)

        val res4 = ExpressionEvaluator.evaluate("20 ÷ 4")
        assertTrue(res4 is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("5", (res4 as ExpressionEvaluator.EvaluationResult.Success).formatted)
    }

    @Test
    fun testOperatorPrecedenceAndParentheses() {
        val res = ExpressionEvaluator.evaluate("2 + 3 × 4")
        assertTrue(res is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("14", (res as ExpressionEvaluator.EvaluationResult.Success).formatted)

        val resParen = ExpressionEvaluator.evaluate("(2 + 3) × 4")
        assertTrue(resParen is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("20", (resParen as ExpressionEvaluator.EvaluationResult.Success).formatted)
    }

    @Test
    fun testDivideByZero() {
        val res = ExpressionEvaluator.evaluate("10 ÷ 0")
        assertTrue(res is ExpressionEvaluator.EvaluationResult.Error)
        assertEquals("Cannot divide by 0", (res as ExpressionEvaluator.EvaluationResult.Error).message)
    }

    @Test
    fun testScientificFunctionsAndTrig() {
        val sinDeg = ExpressionEvaluator.evaluate("sin(30)", isDegrees = true)
        assertTrue(sinDeg is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("0.5", (sinDeg as ExpressionEvaluator.EvaluationResult.Success).formatted)

        val sqrtVal = ExpressionEvaluator.evaluate("√(16)")
        assertTrue(sqrtVal is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("4", (sqrtVal as ExpressionEvaluator.EvaluationResult.Success).formatted)

        val powVal = ExpressionEvaluator.evaluate("2^3")
        assertTrue(powVal is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("8", (powVal as ExpressionEvaluator.EvaluationResult.Success).formatted)

        val factVal = ExpressionEvaluator.evaluate("5!")
        assertTrue(factVal is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("120", (factVal as ExpressionEvaluator.EvaluationResult.Success).formatted)
    }

    @Test
    fun testDecimalRoundingAndFormatting() {
        val res = ExpressionEvaluator.evaluate("0.1 + 0.2")
        assertTrue(res is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("0.3", (res as ExpressionEvaluator.EvaluationResult.Success).formatted)

        val thousand = ExpressionEvaluator.evaluate("1000 × 1000")
        assertTrue(thousand is ExpressionEvaluator.EvaluationResult.Success)
        assertEquals("1,000,000", (thousand as ExpressionEvaluator.EvaluationResult.Success).formatted)
    }

    @Test
    fun testLivePreview() {
        val preview = ExpressionEvaluator.evaluatePreview("12 × 5 + ")
        assertEquals("60", preview)

        val previewParen = ExpressionEvaluator.evaluatePreview("(10 + 5")
        assertEquals("15", previewParen)
    }

    @Test
    fun testViewModelWorkflow() {
        val vm = CalculatorViewModel()
        vm.onDigit("5")
        vm.onOperator("×")
        vm.onDigit("6")
        assertEquals("5×6", vm.uiState.value.expression)
        assertEquals("30", vm.uiState.value.previewResult)

        vm.onEquals()
        assertEquals("30", vm.uiState.value.finalResult)
        assertTrue(vm.uiState.value.history.isNotEmpty())
        assertEquals("5×6", vm.uiState.value.history.first().expression)
        assertEquals("30", vm.uiState.value.history.first().result)

        // Starting new digit replaces previous result
        vm.onDigit("7")
        assertEquals("7", vm.uiState.value.expression)
    }
}
