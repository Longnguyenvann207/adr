package com.example.calculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScientificKeypad(
    onFunction: (String) -> Unit,
    onConstant: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: sin, cos, tan, π
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "sin",
                onClick = { onFunction("sin") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 17.sp,
                minHeight = 48.dp,
                testTag = "btn_sin",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "cos",
                onClick = { onFunction("cos") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 17.sp,
                minHeight = 48.dp,
                testTag = "btn_cos",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "tan",
                onClick = { onFunction("tan") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 17.sp,
                minHeight = 48.dp,
                testTag = "btn_tan",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "π",
                onClick = { onConstant("π") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 18.sp,
                minHeight = 48.dp,
                testTag = "btn_pi",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: ln, log, √, e
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "ln",
                onClick = { onFunction("ln") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 17.sp,
                minHeight = 48.dp,
                testTag = "btn_ln",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "log",
                onClick = { onFunction("log") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 17.sp,
                minHeight = 48.dp,
                testTag = "btn_log",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "√",
                onClick = { onFunction("sqrt") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 19.sp,
                minHeight = 48.dp,
                testTag = "btn_sqrt",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "e",
                onClick = { onConstant("e") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 18.sp,
                minHeight = 48.dp,
                testTag = "btn_e",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: x², x^y, 1/x, x!
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "x²",
                onClick = { onFunction("sqr") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 17.sp,
                minHeight = 48.dp,
                testTag = "btn_sqr",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "xʸ",
                onClick = { onFunction("pow") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 17.sp,
                minHeight = 48.dp,
                testTag = "btn_pow",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "1/x",
                onClick = { onFunction("inv") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 16.sp,
                minHeight = 48.dp,
                testTag = "btn_inv",
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "x!",
                onClick = { onFunction("fact") },
                type = ButtonStyleType.FUNCTION,
                fontSize = 17.sp,
                minHeight = 48.dp,
                testTag = "btn_fact",
                modifier = Modifier.weight(1f)
            )
        }
    }
}
