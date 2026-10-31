package com.renzien.aethra

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.renzien.aethra.data.OnboardingPreferences
import com.renzien.aethra.ui.screens.HomeScreen
import com.renzien.aethra.ui.screens.OnboardingScreen
import com.renzien.aethra.ui.theme.AethraColors
import java.io.IOException
import kotlinx.coroutines.launch

private enum class StartScreen {
    Loading,
    Onboarding,
    Home,
    Error
}

@Composable
fun AethraApp() {
    val appContext = LocalContext.current.applicationContext

    val preferences = remember(appContext) {
        OnboardingPreferences(appContext)
    }

    val scope = rememberCoroutineScope()

    var screen by remember {
        mutableStateOf(StartScreen.Loading)
    }

    var retryCount by remember { mutableStateOf(0) }
    var isSaving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(preferences, retryCount) {
        screen = StartScreen.Loading

        screen = try {
            if (preferences.isCompleted()) {
                StartScreen.Home
            } else {
                StartScreen.Onboarding
            }
        } catch (_: IOException) {
            StartScreen.Error
        }
    }

    AnimatedContent(
        targetState = screen,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AethraColors.Navy,
                        AethraColors.Ocean
                    )
                )
            ),
        transitionSpec = {
            (
                    fadeIn(
                        animationSpec = tween(500)
                    ) + slideInVertically(
                        animationSpec = tween(500),
                        initialOffsetY = { height -> height / 12 }
                    )
                    ) togetherWith fadeOut(
                animationSpec = tween(200)
            )
        },
        label = "Aethra screen transition"
    ) { currentScreen ->
        when (currentScreen) {
            StartScreen.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                }
            }

            StartScreen.Onboarding -> {
                OnboardingScreen(
                    isSaving = isSaving,
                    errorMessage = saveError,
                    onGetStarted = {
                        if (!isSaving) {
                            isSaving = true
                            saveError = null

                            scope.launch {
                                try {
                                    preferences.complete()
                                    screen = StartScreen.Home
                                } catch (_: IOException) {
                                    saveError = "We couldn't save your " +
                                            "progress. Please try again."
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    }
                )
            }

            StartScreen.Home -> {
                HomeScreen()
            }

            StartScreen.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "We couldn't open Aethra. Please try again.",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { retryCount += 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = AethraColors.Navy
                        )
                    ) {
                        Text(text = "Try again")
                    }
                }
            }
        }
    }
}