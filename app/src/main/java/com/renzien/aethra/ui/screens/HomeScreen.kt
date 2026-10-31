package com.renzien.aethra.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.aethra.ui.components.AethraLogo
import com.renzien.aethra.ui.components.GlassCard
import com.renzien.aethra.ui.theme.AethraColors

@Composable
fun HomeScreen(
    cardSurfaceModifier: Modifier = Modifier,
    cardContentModifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        AethraColors.Navy,
                        AethraColors.Ocean
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(
                x = size.width * 0.80f,
                y = size.height * 0.37f
            )
            val radius = size.width * 0.85f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AethraColors.Glow.copy(alpha = 0.65f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                )
        ) {
            AethraLogo()

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Your Finance,\nMore stable.",
                color = Color.White,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(32.dp))

            BalanceCard(
                surfaceModifier = cardSurfaceModifier,
                contentModifier = cardContentModifier
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Example Data",
                color = AethraColors.MutedText,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun BalanceCard(
    surfaceModifier: Modifier,
    contentModifier: Modifier
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        surfaceModifier = surfaceModifier,
        contentModifier = contentModifier
    ) {
        Text(
            text = "Balance Amount",
            color = AethraColors.MutedText,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Rp12.680.420",
            color = Color.White,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    AethraColors.Mint.copy(alpha = 0.12f)
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                text = "+2.46% this month",
                color = AethraColors.Mint,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(28.dp))

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.16f)
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Income",
                    color = AethraColors.MutedText,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Rp15.250.000",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Expense",
                    color = AethraColors.MutedText,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Rp2.569.580",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}