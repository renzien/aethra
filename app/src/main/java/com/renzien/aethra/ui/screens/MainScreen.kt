package com.renzien.aethra.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.renzien.aethra.ui.TransactionSaveState
import com.renzien.aethra.ui.TransactionViewModel
import com.renzien.aethra.ui.components.AddTransactionButton
import com.renzien.aethra.ui.components.AddTransactionSheet
import com.renzien.aethra.ui.components.AethraBackground
import com.renzien.aethra.ui.components.AethraTab
import com.renzien.aethra.ui.components.BottomNavigationBackdrop
import com.renzien.aethra.ui.components.FloatingNavBar
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import java.util.UUID

@Composable
fun MainScreen(
    viewModel: TransactionViewModel,
    navigationEnabled: Boolean = true,
    cardSurfaceModifier: Modifier = Modifier,
    cardContentModifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val transactions = uiState.transactions
    val storageReady =
        !uiState.isLoading && uiState.loadError == null

    var selectedTab by rememberSaveable {
        mutableStateOf(AethraTab.Home)
    }

    var showAddTransaction by rememberSaveable {
        mutableStateOf(false)
    }

    var requestId by rememberSaveable {
        mutableStateOf("")
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

    LaunchedEffect(
        showAddTransaction,
        requestId,
        transactions,
        uiState.saveState
    ) {
        if (
            showAddTransaction &&
            uiState.saveState == TransactionSaveState.Idle &&
            transactions.any { it.id == requestId }
        ) {
            showAddTransaction = false
        }
    }

    BackHandler(
        enabled = navigationEnabled &&
                !showAddTransaction &&
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

            if (uiState.loadError != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.loadError.orEmpty(),
                        color = Color.White
                    )

                    Button(
                        onClick = viewModel::retryLoading
                    ) {
                        Text("Try again")
                    }
                }
            } else {
                AnimatedContent(
                    targetState = selectedTab,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        fadeIn(tween(220)) togetherWith
                                fadeOut(tween(120))
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
                                    bottomContentPadding = contentBottomPadding,
                                    cardSurfaceModifier = cardSurfaceModifier,
                                    cardContentModifier = cardContentModifier
                                )
                            }

                            AethraTab.Transactions -> {
                                TransactionsScreen(
                                    transactions = transactions,
                                    bottomContentPadding = contentBottomPadding
                                )
                            }

                            AethraTab.Insights -> {
                                InsightsScreen(
                                    transactions = transactions,
                                    bottomContentPadding = contentBottomPadding
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    ),
                    color = Color.White
                )
            }
        }

        BottomNavigationBackdrop(
            hazeState = hazeState,
            height = backdropHeight,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )

        AddTransactionButton(
            onClick = {
                viewModel.clearSaveResult()

                requestId = UUID.randomUUID().toString()
                showAddTransaction = true
            },
            enabled = navigationEnabled &&
                    storageReady &&
                    !showAddTransaction,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(
                    end = 24.dp,
                    bottom = navigationHeight + 32.dp
                )
        )

        FloatingNavBar(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
            },
            hazeState = hazeState,
            enabled = navigationEnabled &&
                    storageReady &&
                    !showAddTransaction,
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

    if (showAddTransaction) {
        AddTransactionSheet(
            requestId = requestId,
            saveState = uiState.saveState,
            onDismiss = {
                showAddTransaction = false
                viewModel.clearSaveResult()
            },
            onSubmit = { draft ->
                viewModel.submit(
                    draft = draft,
                    requestId = requestId
                )
            }
        )
    }
}