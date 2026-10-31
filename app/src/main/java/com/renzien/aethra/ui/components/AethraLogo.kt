package com.renzien.aethra.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AethraLogo(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Aethra",
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    )
}