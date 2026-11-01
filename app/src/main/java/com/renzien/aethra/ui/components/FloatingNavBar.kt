package com.renzien.aethra.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.aethra.ui.theme.AethraColors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

enum class AethraTab(val title: String) {
    Home("Home"),
    Transactions("Transactions"),
    Insights("Insights")
}

@Composable
fun FloatingNavBar(
    selectedTab: AethraTab,
    onTabSelected: (AethraTab) -> Unit,
    hazeState: HazeState,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(32.dp)
    val glassStyle = HazeStyle(
        backgroundColor = AethraColors.Navy,
        tints = listOf(
            HazeTint(AethraColors.Navy.copy(alpha = 0.42f)),
            HazeTint(Color.White.copy(alpha = 0.08f))
        ),
        blurRadius = 24.dp,
        noiseFactor = 0.03f,
        fallbackTint = HazeTint(AethraColors.Navy.copy(alpha = 0.94f))
    )

    Row(
        modifier = modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .shadow(18.dp, shape = shape, clip = false)
            .clip(shape)
            .hazeEffect(state = hazeState, style = glassStyle)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.32f),
                        Color.White.copy(alpha = 0.10f)
                    )
                ),
                shape = shape
            )
            .padding(6.dp)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AethraTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            val pillColor by animateColorAsState(
                targetValue = if (selected) Color.White.copy(alpha = 0.16f)
                else Color.Transparent,
                animationSpec = tween(220),
                label = "${tab.name} pill"
            )
            val foreground by animateColorAsState(
                targetValue = if (selected) AethraColors.Mint
                else AethraColors.MutedText,
                animationSpec = tween(220),
                label = "${tab.name} foreground"
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(pillColor)
                    .selectable(
                        selected = selected,
                        enabled = enabled,
                        role = Role.Tab,
                        onClick = { onTabSelected(tab) }
                    )
                    .padding(horizontal = 4.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NavigationIcon(tab = tab, tint = foreground)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = tab.title,
                    color = foreground,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun NavigationIcon(tab: AethraTab, tint: Color) {
    Canvas(Modifier.size(22.dp)) {
        val stroke = 1.8.dp.toPx()
        fun point(x: Float, y: Float) = Offset(size.width * x, size.height * y)

        when (tab) {
            AethraTab.Home -> {
                val path = Path().apply {
                    moveTo(size.width * 0.12f, size.height * 0.45f)
                    lineTo(size.width * 0.5f, size.height * 0.13f)
                    lineTo(size.width * 0.88f, size.height * 0.45f)
                    moveTo(size.width * 0.23f, size.height * 0.4f)
                    lineTo(size.width * 0.23f, size.height * 0.85f)
                    lineTo(size.width * 0.77f, size.height * 0.85f)
                    lineTo(size.width * 0.77f, size.height * 0.4f)
                    moveTo(size.width * 0.43f, size.height * 0.85f)
                    lineTo(size.width * 0.43f, size.height * 0.62f)
                    lineTo(size.width * 0.59f, size.height * 0.62f)
                    lineTo(size.width * 0.59f, size.height * 0.85f)
                }
                drawPath(path, tint, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            AethraTab.Transactions -> {
                drawRoundRect(
                    color = tint,
                    topLeft = point(0.18f, 0.12f),
                    size = Size(size.width * 0.64f, size.height * 0.76f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
                    style = Stroke(stroke)
                )
                listOf(0.32f, 0.50f, 0.68f).forEach { y ->
                    drawLine(tint, point(0.34f, y), point(0.66f, y), stroke, StrokeCap.Round)
                }
            }
            AethraTab.Insights -> {
                listOf(0.25f to 0.55f, 0.5f to 0.25f, 0.75f to 0.4f).forEach { (x, top) ->
                    drawLine(tint, point(x, 0.84f), point(x, top), stroke * 1.6f, StrokeCap.Round)
                }
            }
        }
    }
}
