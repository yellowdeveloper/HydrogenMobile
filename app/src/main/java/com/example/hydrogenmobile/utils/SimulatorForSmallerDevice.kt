package com.example.hydrogenmobile.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun ApplicationSimulator(
    targetWidth: Dp = 1280.dp,
    targetHeight: Dp = 800.dp,
    content: @Composable () -> Unit
){
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val scaleX = screenWidth / targetWidth
        val scaleY = screenHeight / targetHeight
        val scale = min(scaleX, scaleY)

        Box(
            modifier = Modifier
                .requiredSize(targetWidth, targetHeight)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                )
                .background(Color.White)
        ) {
            content()
        }

    }
}