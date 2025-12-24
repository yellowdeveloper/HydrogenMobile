package com.example.hydrogenmobile.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

// DRAW BORDER FUNCTION
fun Modifier.leftBorder(width: Dp, color: Color): Modifier = composed {
    val density = LocalDensity.current
    val strokeWidth = with(density) { width.toPx() }

    Modifier.drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = strokeWidth
        )
    }
}
fun Modifier.rightBorder(width: Dp, color: Color    ): Modifier = composed {
    val density = LocalDensity.current
    val strokeWidth = with(density) { width.toPx() }

    Modifier.drawBehind {
        drawLine(
            color = color,
            start = Offset(size.width, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth
        )
    }
}
fun Modifier.bottomBorder(width: Dp, color: Color): Modifier = composed {
    val density = LocalDensity.current
    val strokeWidth = with(density) { width.toPx() }

    Modifier.drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth
        )
    }
}
fun Modifier.topBorder(width: Dp, color: Color): Modifier = composed {
    val density = LocalDensity.current
    val strokeWidth = with(density) { width.toPx() }

    Modifier.drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidth
        )
    }
}