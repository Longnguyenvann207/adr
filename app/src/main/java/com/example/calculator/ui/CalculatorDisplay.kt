package com.example.calculator.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.viewmodel.CalculatorUiState

@Composable
fun CalculatorDisplay(
    uiState: CalculatorUiState,
    onToggleAngleMode: () -> Unit,
    onToggleScientific: () -> Unit,
    onToggleHistory: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exprScrollState = rememberScrollState()

    LaunchedEffect(uiState.expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        // Top status and control toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Degree/Radian Mode toggle chip
                Surface(
                    onClick = onToggleAngleMode,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.testTag("angle_mode_toggle")
                ) {
                    Text(
                        text = if (uiState.isDegreeMode) "DEG" else "RAD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Memory Active Badge
                if (uiState.memoryValue != 0.0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.testTag("memory_badge")
                    ) {
                        Text(
                            text = "M",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Action icons: Scientific toggle, Copy, History
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleScientific,
                    modifier = Modifier.testTag("scientific_toggle_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Scientific Functions",
                        tint = if (uiState.isScientificExpanded) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.testTag("copy_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Expression or Result",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onToggleHistory,
                    modifier = Modifier.testTag("history_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (uiState.history.isNotEmpty()) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(
                                        text = uiState.history.size.toString(),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Calculation History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input expression line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(exprScrollState),
            horizontalArrangement = Arrangement.End
        ) {
            val displayExpression = if (uiState.expression.isEmpty() && uiState.finalResult == null) {
                "0"
            } else {
                uiState.expression
            }

            Text(
                text = displayExpression,
                fontSize = if (uiState.isResultDisplayed) 26.sp else 38.sp,
                fontWeight = if (uiState.isResultDisplayed) FontWeight.Normal else FontWeight.Bold,
                color = if (uiState.isResultDisplayed) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.End,
                modifier = Modifier.testTag("expression_display_text")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Result / Live Preview line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            when {
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("error_display_text")
                    )
                }
                uiState.finalResult != null -> {
                    Text(
                        text = "= ${uiState.finalResult}",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("final_result_display_text")
                    )
                }
                uiState.previewResult != null -> {
                    Text(
                        text = "= ${uiState.previewResult}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("preview_result_display_text")
                    )
                }
            }
        }
    }
}
