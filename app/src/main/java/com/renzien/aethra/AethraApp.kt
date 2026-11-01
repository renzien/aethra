package com.renzien.aethra

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.renzien.aethra.data.OnboardingPreferences
import com.renzien.aethra.ui.TransactionViewModel
import com.renzien.aethra.ui.screens.MainScreen
import com.renzien.aethra.ui.screens.OnboardingScreen
import com.renzien.aethra.ui.theme.AethraColors
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private enum class StartScreen {
    Loading,
    Onboarding,
    Home,
    Error
}

private const val WALLET_DURATION = 1200

private val walletBoundsTransform = BoundsTransform { start, end ->
    val zoom = 1.24f
    val halfWidth = end.width * zoom / 2f
    val halfHeight = end.height * zoom / 2f

    val closeUp = Rect(
        left = end.center.x - halfWidth,
        top = end.center.y - halfHeight,
        right = end.center.x + halfWidth,
        bottom = end.center.y + halfHeight
    )

    keyframes {
        durationMillis = WALLET_DURATION

        start at 0 using FastOutSlowInEasing
        closeUp at 700 using FastOutSlowInEasing
        end at WALLET_DURATION
    }
}

@Composable
fun AethraApp() {
    val appContext = LocalContext.current.applicationContext

    val preferences = remember(appContext) {
        OnboardingPreferences(appContext)
    }

    val scope = rememberCoroutineScope()

    val transactionFactory = remember(appContext) {
        TransactionViewModel.factory(appContext)
    }

    val transactionViewModel: TransactionViewModel = viewModel(
        factory = transactionFactory
    )

    var screen by remember {
        mutableStateOf(StartScreen.Loading)
    }

    var retryCount by remember {
        mutableStateOf(0)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var saveError by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(preferences, retryCount) {
        screen = StartScreen.Loading

        screen = try {
            val completed = preferences.isCompleted()

            // Siapkan data kartu tujuan sebelum animasi onboarding dimulai.
            val loaded = transactionViewModel.uiState.first {
                !it.isLoading
            }

            if (loaded.loadError != null) {
                StartScreen.Error
            } else if (completed) {
                StartScreen.Home
            } else {
                StartScreen.Onboarding
            }
        } catch (_: IOException) {
            StartScreen.Error
        }
    }

    SharedTransitionLayout(
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
        val sharedScope = this

        AnimatedContent(
            targetState = screen,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                val isWalletTransition =
                    initialState == StartScreen.Onboarding &&
                            targetState == StartScreen.Home

                if (isWalletTransition) {
                    fadeIn(
                        tween(
                            durationMillis = 400,
                            delayMillis = 800
                        )
                    ) togetherWith fadeOut(
                        tween(
                            durationMillis = 200
                        )
                    )
                } else {
                    fadeIn(
                        tween(250)
                    ) togetherWith fadeOut(
                        tween(150)
                    )
                }
            },
            label = "Aethra wallet zoom"
        ) { currentScreen ->
            val visibilityScope = this

            val surfaceModifier = with(sharedScope) {
                Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = "aethra-wallet-surface"
                    ),
                    animatedVisibilityScope = visibilityScope,
                    boundsTransform = walletBoundsTransform,
                    zIndexInOverlay = 1f
                )
            }

            val contentModifier = with(sharedScope) {
                Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = "aethra-wallet-content"
                    ),
                    animatedVisibilityScope = visibilityScope,
                    boundsTransform = walletBoundsTransform,
                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(
                        contentScale = ContentScale.Fit
                    ),
                    enter = fadeIn(
                        tween(
                            durationMillis = 300,
                            delayMillis = 350
                        )
                    ),
                    exit = fadeOut(
                        tween(
                            durationMillis = 200,
                            delayMillis = 200
                        )
                    ),
                    zIndexInOverlay = 2f
                )
            }

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
                        cardSurfaceModifier = surfaceModifier,
                        cardContentModifier = contentModifier,
                        isSaving = isSaving,
                        errorMessage = saveError,
                        onGetStarted = {
                            if (
                                !isSaving &&
                                screen == StartScreen.Onboarding
                            ) {
                                isSaving = true
                                saveError = null

                                scope.launch {
                                    try {
                                        preferences.complete()
                                        screen = StartScreen.Home
                                    } catch (_: IOException) {
                                        saveError =
                                            "We couldn't save your progress. Please try again."
                                    } finally {
                                        isSaving = false
                                    }
                                }
                            }
                        }
                    )
                }

                StartScreen.Home -> {
                    MainScreen(
                        viewModel = transactionViewModel,
                        navigationEnabled = !sharedScope.isTransitionActive,
                        cardSurfaceModifier = surfaceModifier,
                        cardContentModifier = contentModifier
                    )
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

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                transactionViewModel.retryLoading()
                                retryCount += 1
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = AethraColors.Navy
                            )
                        ) {
                            Text("Try again")
                        }
                    }
                }
            }
        }
    }
}