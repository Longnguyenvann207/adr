package com.example.calculator.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorKeypad(
    isScientificExpanded: Boolean,
    isExpressionEmpty: Boolean,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (String) -> Unit,
    onParenthesis: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onToggleSign: () -> Unit,
    onPercent: () -> Unit,
    onEquals: () -> Unit,
    onFunction: (String) -> Unit,
    onConstant: (String) -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Memory & Quick Actions Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "MC",
                onClick = onMemoryClear,
                type = ButtonStyleType.SECONDARY,
                fontSize = 14.sp,
                minHeight = 42.dp,
                testTag = "btn_mc",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "MR",
                onClick = onMemoryRecall,
                type = ButtonStyleType.SECONDARY,
                fontSize = 14.sp,
                minHeight = 42.dp,
                testTag = "btn_mr",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "M+",
                onClick = onMemoryAdd,
                type = ButtonStyleType.SECONDARY,
                fontSize = 14.sp,
                minHeight = 42.dp,
                testTag = "btn_m_plus",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "M−",
                onClick = onMemorySubtract,
                type = ButtonStyleType.SECONDARY,
                fontSize = 14.sp,
                minHeight = 42.dp,
                testTag = "btn_m_minus",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "",
                onClick = onBackspace,
                type = ButtonStyleType.ACTION,
                minHeight = 42.dp,
                testTag = "btn_backspace",
                modifier = Modifier.weight(1f),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }

        // Expandable Scientific Keypad
        AnimatedVisibility(
            visible = isScientificExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            ScientificKeypad(
                onFunction = onFunction,
                onConstant = onConstant
            )
        }

        // Row 1: AC/C, ( ), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = if (isExpressionEmpty) "AC" else "C",
                onClick = onClear,
                type = ButtonStyleType.ACTION,
                fontSize = 22.sp,
                minHeight = 60.dp,
                testTag = "btn_clear",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "( )",
                onClick = onParenthesis,
                type = ButtonStyleType.FUNCTION,
                fontSize = 20.sp,
                minHeight = 60.dp,
                testTag = "btn_parenthesis",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "%",
                onClick = onPercent,
                type = ButtonStyleType.FUNCTION,
                fontSize = 22.sp,
                minHeight = 60.dp,
                testTag = "btn_percent",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "÷",
                onClick = { onOperator("÷") },
                type = ButtonStyleType.OPERATOR,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_divide",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "7",
                onClick = { onDigit("7") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_7",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "8",
                onClick = { onDigit("8") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_8",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "9",
                onClick = { onDigit("9") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_9",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "×",
                onClick = { onOperator("×") },
                type = ButtonStyleType.OPERATOR,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_multiply",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "4",
                onClick = { onDigit("4") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_4",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "5",
                onClick = { onDigit("5") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_5",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "6",
                onClick = { onDigit("6") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_6",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "−",
                onClick = { onOperator("−") },
                type = ButtonStyleType.OPERATOR,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_minus",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "1",
                onClick = { onDigit("1") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_1",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "2",
                onClick = { onDigit("2") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_2",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "3",
                onClick = { onDigit("3") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_3",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "+",
                onClick = { onOperator("+") },
                type = ButtonStyleType.OPERATOR,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_plus",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 5: ±, 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "±",
                onClick = onToggleSign,
                type = ButtonStyleType.NUMBER,
                fontSize = 22.sp,
                minHeight = 60.dp,
                testTag = "btn_sign",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "0",
                onClick = { onDigit("0") },
                type = ButtonStyleType.NUMBER,
                fontSize = 26.sp,
                minHeight = 60.dp,
                testTag = "btn_0",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = ".",
                onClick = onDecimal,
                type = ButtonStyleType.NUMBER,
                fontSize = 28.sp,
                minHeight = 60.dp,
                testTag = "btn_decimal",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "=",
                onClick = onEquals,
                type = ButtonStyleType.OPERATOR,
                fontSize = 28.sp,
                minHeight = 60.dp,
                testTag = "btn_equals",
                modifier = Modifier.weight(1f)
            )
        }
    }
}
