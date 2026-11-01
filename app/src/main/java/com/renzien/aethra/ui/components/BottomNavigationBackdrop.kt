package com.renzien.aethra.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.renzien.aethra.ui.theme.AethraColors
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@OptIn(ExperimentalHazeApi::class)
@Composable
fun BottomNavigationBackdrop(
    hazeState: HazeState,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val style = remember {
        HazeStyle(
            backgroundColor = AethraColors.Ocean,
            tint = HazeTint(
                AethraColors.Ocean.copy(alpha = 0.14f)
            ),
            blurRadius = 32.dp,
            noiseFactor = 0f,
            fallbackTint = HazeTint(
                AethraColors.Ocean.copy(alpha = 0.94f)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .hazeEffect(
                state = hazeState,
                style = style
            ) {
                progressive = HazeProgressive.verticalGradient(
                    easing = LinearEasing,
                    startIntensity = 0f,
                    endIntensity = 1f
                )

                mask = Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.30f to Color.Black.copy(alpha = 0.45f),
                    0.65f to Color.Black,
                    1f to Color.Black
                )
            }
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.30f to AethraColors.Ocean.copy(
                        alpha = 0.06f
                    ),
                    0.65f to AethraColors.Ocean.copy(
                        alpha = 0.60f
                    ),
                    1f to AethraColors.Ocean.copy(
                        alpha = 0.98f
                    )
                )
            )
    )
}