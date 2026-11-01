package com.renzien.aethra.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.aethra.ui.components.AethraLogo
import com.renzien.aethra.ui.components.GlassCard
import com.renzien.aethra.ui.theme.AethraColors

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    isSaving: Boolean,
    errorMessage: String?,
    cardSurfaceModifier: Modifier = Modifier,
    cardContentModifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AethraLogo()

            Text(
                text = "A clearer view\nof your money.",
                color = Color.White,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Medium
            )

            WalletArtwork(
                surfaceModifier = cardSurfaceModifier,
                contentModifier = cardContentModifier
            )

            Text(
                text = "See your balance, track your spending, " +
                        "and build better money habits.",
                color = AethraColors.MutedText,
                fontSize = 16.sp,
                lineHeight = 26.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = onGetStarted,
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = AethraColors.Navy,
                    disabledContainerColor = Color.White.copy(
                        alpha = 0.70f
                    ),
                    disabledContentColor = AethraColors.Navy
                )
            ) {
                Text(
                    text = if (isSaving) {
                        "Getting ready..."
                    } else {
                        "Get started"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun WalletArtwork(
    surfaceModifier: Modifier,
    contentModifier: Modifier
) {
    val transition = rememberInfiniteTransition(
        label = "wallet floating"
    )

    val lift by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wallet vertical movement"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 16.dp)
                .graphicsLayer {
                    rotationZ = 6f
                    translationY = lift.dp.toPx()
                }
                .background(
                    color = Color.White.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(28.dp)
                )
        )

        GlassCard(
            modifier = Modifier.fillMaxWidth(0.90f),
            surfaceModifier = surfaceModifier,
            contentModifier = contentModifier
        ) {
            Text(
                text = "Aethra wallet",
                color = AethraColors.MutedText,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Small steps.\nStronger habits.",
                color = Color.White,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "TRACK  ·  PLAN  ·  GROW",
                color = AethraColors.Mint,
                fontSize = 11.sp,
                letterSpacing = 0.8.sp
            )
        }
    }
}