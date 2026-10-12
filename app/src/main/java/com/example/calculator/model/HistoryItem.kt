package com.example.calculator.model

import java.util.UUID

data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
