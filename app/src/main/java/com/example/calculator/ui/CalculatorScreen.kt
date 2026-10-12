package com.example.calculator.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Calculator", text)
        clipboard.setPrimaryClip(clip)
        scope.launch {
            snackbarHostState.showSnackbar("Copied: $text")
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("calculator_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                // Landscape / Wide screen side-by-side layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Left half: Display and controls
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        CalculatorDisplay(
                            uiState = uiState,
                            onToggleAngleMode = { viewModel.onToggleAngleMode() },
                            onToggleScientific = { viewModel.onToggleScientific() },
                            onToggleHistory = { viewModel.onToggleHistory(true) },
                            onCopy = {
                                val textToCopy = uiState.finalResult ?: uiState.expression
                                if (textToCopy.isNotEmpty()) {
                                    copyToClipboard(textToCopy)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Right half: Keypad
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxSize()
                            .padding(start = 12.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        CalculatorKeypad(
                            isScientificExpanded = uiState.isScientificExpanded,
                            isExpressionEmpty = uiState.expression.isEmpty(),
                            onDigit = { viewModel.onDigit(it) },
                            onDecimal = { viewModel.onDecimal() },
                            onOperator = { viewModel.onOperator(it) },
                            onParenthesis = { viewModel.onParenthesis() },
                            onClear = { viewModel.onClear() },
                            onBackspace = { viewModel.onBackspace() },
                            onToggleSign = { viewModel.onToggleSign() },
                            onPercent = { viewModel.onPercent() },
                            onEquals = { viewModel.onEquals() },
                            onFunction = { viewModel.onFunction(it) },
                            onConstant = { viewModel.onConstant(it) },
                            onMemoryAdd = { viewModel.onMemoryAdd() },
                            onMemorySubtract = { viewModel.onMemorySubtract() },
                            onMemoryRecall = { viewModel.onMemoryRecall() },
                            onMemoryClear = { viewModel.onMemoryClear() }
                        )
                    }
                }
            } else {
                // Portrait Layout
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 520.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Display takes available top space
                        CalculatorDisplay(
                            uiState = uiState,
                            onToggleAngleMode = { viewModel.onToggleAngleMode() },
                            onToggleScientific = { viewModel.onToggleScientific() },
                            onToggleHistory = { viewModel.onToggleHistory(true) },
                            onCopy = {
                                val textToCopy = uiState.finalResult ?: uiState.expression
                                if (textToCopy.isNotEmpty()) {
                                    copyToClipboard(textToCopy)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Keypad anchored to the bottom
                        CalculatorKeypad(
                            isScientificExpanded = uiState.isScientificExpanded,
                            isExpressionEmpty = uiState.expression.isEmpty(),
                            onDigit = { viewModel.onDigit(it) },
                            onDecimal = { viewModel.onDecimal() },
                            onOperator = { viewModel.onOperator(it) },
                            onParenthesis = { viewModel.onParenthesis() },
                            onClear = { viewModel.onClear() },
                            onBackspace = { viewModel.onBackspace() },
                            onToggleSign = { viewModel.onToggleSign() },
                            onPercent = { viewModel.onPercent() },
                            onEquals = { viewModel.onEquals() },
                            onFunction = { viewModel.onFunction(it) },
                            onConstant = { viewModel.onConstant(it) },
                            onMemoryAdd = { viewModel.onMemoryAdd() },
                            onMemorySubtract = { viewModel.onMemorySubtract() },
                            onMemoryRecall = { viewModel.onMemoryRecall() },
                            onMemoryClear = { viewModel.onMemoryClear() },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }

        // History Modal Bottom Sheet
        if (uiState.isHistoryVisible) {
            HistoryBottomSheet(
                sheetState = sheetState,
                history = uiState.history,
                onDismiss = { viewModel.onToggleHistory(false) },
                onSelectItem = { item -> viewModel.onSelectHistoryItem(item) },
                onClearHistory = { viewModel.onClearHistory() }
            )
        }
    }
}
