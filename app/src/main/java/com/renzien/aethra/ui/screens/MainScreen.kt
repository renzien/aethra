package com.renzien.aethra.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.renzien.aethra.data.SampleTransactions
import com.renzien.aethra.ui.components.AethraBackground
import com.renzien.aethra.ui.components.AethraTab
import com.renzien.aethra.ui.components.BottomNavigationBackdrop
import com.renzien.aethra.ui.components.FloatingNavBar
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun MainScreen(
    navigationEnabled: Boolean = true,
    cardSurfaceModifier: Modifier = Modifier,
    cardContentModifier: Modifier = Modifier
) {
    val transactions = remember {
        SampleTransactions.create()
    }

    var selectedTab by rememberSaveable {
        mutableStateOf(AethraTab.Home)
    }

    val stateHolder = rememberSaveableStateHolder()
    val hazeState = rememberHazeState()
    val density = LocalDensity.current

    var navigationHeight by remember {
        mutableStateOf(72.dp)
    }

    val bottomSystemInset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    val backdropHeight =
        navigationHeight + bottomSystemInset + 96.dp

    val contentBottomPadding =
        navigationHeight + 112.dp

    BackHandler(
        enabled = navigationEnabled &&
                selectedTab != AethraTab.Home
    ) {
        selectedTab = AethraTab.Home
    }

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
        ) {
            AethraBackground()

            AnimatedContent(
                targetState = selectedTab,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    fadeIn(
                        tween(220)
                    ) togetherWith fadeOut(
                        tween(120)
                    )
                },
                label = "main tab transition"
            ) { tab ->
                stateHolder.SaveableStateProvider(
                    key = tab.name
                ) {
                    when (tab) {
                        AethraTab.Home -> {
                            HomeScreen(
                                transactions = transactions,
                                bottomContentPadding =
                                    contentBottomPadding,
                                cardSurfaceModifier =
                                    cardSurfaceModifier,
                                cardContentModifier =
                                    cardContentModifier
                            )
                        }

                        AethraTab.Transactions -> {
                            TransactionsScreen(
                                transactions = transactions,
                                bottomContentPadding =
                                    contentBottomPadding
                            )
                        }

                        AethraTab.Insights -> {
                            InsightsScreen(
                                transactions = transactions,
                                bottomContentPadding =
                                    contentBottomPadding
                            )
                        }
                    }
                }
            }
        }

        BottomNavigationBackdrop(
            hazeState = hazeState,
            height = backdropHeight,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )

        FloatingNavBar(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
            },
            hazeState = hazeState,
            enabled = navigationEnabled,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 16.dp
                )
                .onSizeChanged {
                    navigationHeight = with(density) {
                        it.height.toDp()
                    }
                }
        )
    }
}