package com.example.calculator.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonStyleType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    ACTION,
    SECONDARY
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ButtonStyleType = ButtonStyleType.NUMBER,
    testTag: String = "btn_$text",
    fontSize: TextUnit = 24.sp,
    minHeight: Dp = 64.dp,
    icon: (@Composable () -> Unit)? = null
) {
    val view = LocalView.current
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    val backgroundColor = when (type) {
        ButtonStyleType.NUMBER -> if (isDark) Color(0xFF232731) else Color(0xFFFFFFFF)
        ButtonStyleType.OPERATOR -> MaterialTheme.colorScheme.primary
        ButtonStyleType.FUNCTION -> if (isDark) Color(0xFF2B313F) else Color(0xFFE2E7F0)
        ButtonStyleType.ACTION -> if (isDark) Color(0xFF382327) else Color(0xFFFFEBEE)
        ButtonStyleType.SECONDARY -> if (isDark) Color(0xFF1E212B) else Color(0xFFEFF1F6)
    }

    val contentColor = when (type) {
        ButtonStyleType.NUMBER -> if (isDark) Color(0xFFF3F5FA) else Color(0xFF181A20)
        ButtonStyleType.OPERATOR -> Color.White
        ButtonStyleType.FUNCTION -> if (isDark) Color(0xFF70B6FF) else Color(0xFF007AFF)
        ButtonStyleType.ACTION -> if (isDark) Color(0xFFFF6B6B) else Color(0xFFE53935)
        ButtonStyleType.SECONDARY -> if (isDark) Color(0xFF9CA3AF) else Color(0xFF5F6877)
    }

    val shape = RoundedCornerShape(20.dp)

    Surface(
        shape = shape,
        color = backgroundColor,
        tonalElevation = if (type == ButtonStyleType.NUMBER && !isDark) 2.dp else 0.dp,
        shadowElevation = if (type == ButtonStyleType.NUMBER && !isDark) 1.dp else 0.dp,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = minHeight)
            .testTag(testTag)
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = contentColor.copy(alpha = 0.25f)),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onClick()
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                icon()
            } else {
                Text(
                    text = text,
                    fontSize = fontSize,
                    fontWeight = if (type == ButtonStyleType.OPERATOR) FontWeight.Bold else FontWeight.SemiBold,
                    color = contentColor
                )
            }
        }
    }
}
