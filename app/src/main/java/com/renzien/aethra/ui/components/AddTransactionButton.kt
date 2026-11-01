package com.renzien.aethra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.renzien.aethra.ui.theme.AethraColors

@Composable
fun AddTransactionButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(14.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(listOf(Color.White, AethraColors.Mint))
            )
            .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = "Add transaction" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(24.dp)) {
            val stroke = 2.4.dp.toPx()
            drawLine(
                color = AethraColors.Navy,
                start = Offset(size.width / 2f, 3.dp.toPx()),
                end = Offset(size.width / 2f, size.height - 3.dp.toPx()),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = AethraColors.Navy,
                start = Offset(3.dp.toPx(), size.height / 2f),
                end = Offset(size.width - 3.dp.toPx(), size.height / 2f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}
