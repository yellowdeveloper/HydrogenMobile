package com.example.hydrogenmobile.utils

fun formatNumber(value: Int?): String {
    if (value == null) return "0"

    val absValue = kotlin.math.abs(value)

    return when {
        absValue >= 1_000_000_000 -> String.format("%.1fb", (value / 1_000_000_000).toFloat())
        absValue >= 1_000_000 -> String.format("%.1fm", (value / 1_000_000).toFloat())
        absValue >= 1_000 -> String.format("%.1fk", (value / 1_000).toFloat())
        else -> value.toInt().toString()
    }
}