package com.example.hydrogenmobile.utils

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import java.util.LinkedList

// Graph Drawing For Data Visualization
@Composable
fun DrawGraph(modifier: Modifier = Modifier, FilterName:String, data: Int, MaxY: Int, MinY: Int, xScale: Int, buffer: List<Int>) {
    val currentXPosition = buffer.lastIndex
//    val xRatio = (currentXPosition.toFloat() / xScale).coerceIn(0f, 1f)
//    val yRatio = ((data - MinY).toFloat() / (MaxY - MinY)).coerceIn(0f, 1f)
    var color = Color.Black


    when {
        FilterName.contains("RAW") -> color = Color(0xFFE77032)
        FilterName.contains("Sample") -> color = Color(0xFF4DA62E)
        FilterName.contains("Low") -> color = Color(0xFF0F9DD3)
        FilterName.contains("Moving") -> color = Color(0xFF9F2B92)
        else -> color = Color.Black
    }

    val path = remember { androidx.compose.ui.graphics.Path() }

    Canvas(modifier = modifier) {
        if (buffer.size < 2) return@Canvas

        path.reset()

        val axisYRange = (MaxY - MinY).toFloat().coerceAtLeast(1f)
        val axisXTick = size.width / (xScale - 1)

        buffer.forEachIndexed { index, value ->
            val yRatio = ((value - MinY).toFloat() / axisYRange).coerceIn(0f, 1f)
            val xPos = index * axisXTick
            val yPos = size.height - (size.height * yRatio)

            drawCircle(
                color = Color.Black,
                center = Offset(xPos, yPos),
                radius = 4f
            )

            if (index == 0) {
                path.moveTo(xPos, yPos)
            }
            else {
                path.lineTo(xPos, yPos)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 5f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
//    Canvas(modifier = modifier) {
//        if (buffer.size < 2) return@Canvas
//
//        for(i in 1 until currentXPosition + 1) {
//            val xRatioBefore = ((i-1).toFloat() / xScale).coerceIn(0f, 1f)
//            val xRatio = (i.toFloat() / xScale).coerceIn(0f, 1f)
//            val yRatioBefore = ((buffer[i-1] - MinY).toFloat() / (MaxY - MinY)).coerceIn(0f, 1f)
//            val yRatio = ((buffer[i] - MinY).toFloat() / (MaxY - MinY)).coerceIn(0f, 1f)
//
//
//            val heightBefore = size.height - (size.height * yRatioBefore)
//            val widthBefore = size.width * xRatioBefore
//
//            val height = size.height - (size.height * yRatio)
//            val width = size.width * xRatio
//
//
//            drawCircle(
//                color = Color.Black,
//                center = Offset(width, height),
//                radius = 5f
//            )
//
//            drawLine(
//                start = Offset(x = widthBefore, y = heightBefore),
//                end = Offset(x = width, y = height),
//                strokeWidth = 5f,
//                color = color
//            )
//        }

//    Canvas(modifier = modifier) {
//        val height = size.height - (size.height * yRatio)
//        val width = size.width * xRatio
//
//        drawCircle(
//            color = Color.Black,
//            center = Offset(width, height),
//            radius = 18f
//        )
//    }
}

// Bar Drawing For Visualize Value Stabilization
@Composable
fun DrawRectangle(modifier: Modifier = Modifier, data: Int, errorThreshold: Int?) {
    var Tresh = 0
    if (errorThreshold == null) Tresh = -100
    else Tresh = errorThreshold

    val ratio = (data.toFloat() / Tresh).coerceIn(0f, 1f)

    // Set Bar Color Base On Ratio
    var barColor = lerp(start = Color.Green, stop = Color.Red, fraction = ratio)

    if (Tresh < 0) barColor = Color.Black

    Canvas(modifier = modifier) {
        drawRect(
            color = barColor,
            topLeft = Offset(0f, 0f),
            size = size
        )
    }
}

// Circle Drawing for Bluetooth Connection Status
@Composable
fun CircleDrawing(modifier: Modifier = Modifier, status: Color, radius: Float) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = status,
            center = center,
            radius = radius
        )
    }
}