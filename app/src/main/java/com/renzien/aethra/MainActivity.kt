package com.renzien.aethra

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.renzien.aethra.ui.screens.HomeScreen
import com.renzien.aethra.ui.theme.AethraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                AndroidColor.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                AndroidColor.TRANSPARENT
            )
        )

        setContent {
            AethraTheme {
                HomeScreen()
            }
        }
    }
}