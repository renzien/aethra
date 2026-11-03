package com.renzien.aethra.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.aethra.data.Transaction
import com.renzien.aethra.data.calculateExpenseCategories
import com.renzien.aethra.data.calculateSummary
import com.renzien.aethra.ui.filterTransactionsByPeriod
import com.renzien.aethra.ui.formatTransactionPeriod
import com.renzien.aethra.ui.components.AethraLogo
import com.renzien.aethra.ui.components.GlassCard
import com.renzien.aethra.ui.components.PeriodSelector
import com.renzien.aethra.ui.formatRupiah
import com.renzien.aethra.ui.theme.AethraColors
import java.time.YearMonth
import java.util.Locale

@Composable
fun InsightsScreen(
    transactions: List<Transaction>,
    bottomContentPadding: Dp = 116.dp,
    selectedMonth: YearMonth? = null,
    onMonthSelected: (YearMonth?) -> Unit = {}
) {
    val periodTransactions = remember(
        transactions,
        selectedMonth
    ) {
        filterTransactionsByPeriod(
            transactions = transactions,
            month = selectedMonth
        )
    }

    val summary = remember(periodTransactions) {
        calculateSummary(periodTransactions)
    }

    val categories = remember(periodTransactions) {
        calculateExpenseCategories(periodTransactions)
    }

    val listState = rememberLazyListState()

    val periodKey = selectedMonth?.toString() ?: "all"

    var lastPeriodKey by rememberSaveable {
        mutableStateOf(periodKey)
    }

    LaunchedEffect(periodKey) {
        if (lastPeriodKey != periodKey) {
            listState.scrollToItem(0)
            lastPeriodKey = periodKey
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentPadding = PaddingValues(
            start = 24.dp,
            top = 32.dp,
            end = 24.dp,
            bottom = bottomContentPadding
        ),
        verticalArrangement = Arrangement.spacedBy(
            16.dp
        )
    ) {
        item(key = "insights-header") {
            Column {
                AethraLogo()

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Insights",
                    color = Color.White,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "A clearer picture of your spending.",
                    color = AethraColors.MutedText,
                    fontSize = 14.sp
                )
            }
        }

        item(key = "period-selector") {
            PeriodSelector(
                transactions = transactions,
                selectedMonth = selectedMonth,
                onMonthSelected = onMonthSelected
            )
        }

        item(key = "cash-flow-summary") {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cash flow",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = formatTransactionPeriod(
                        selectedMonth
                    ),
                    color = AethraColors.MutedText,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(20.dp))

                SummaryRow(
                    label = "Income",
                    amount = formatRupiah(summary.income),
                    tint = AethraColors.Mint
                )

                Spacer(Modifier.height(12.dp))

                SummaryRow(
                    label = "Expense",
                    amount = formatRupiah(summary.expense),
                    tint = Color(0xFFFFB9B3)
                )

                Spacer(Modifier.height(16.dp))

                HorizontalDivider(
                    color = Color.White.copy(
                        alpha = 0.16f
                    )
                )

                Spacer(Modifier.height(16.dp))

                SummaryRow(
                    label = "Net cash flow",
                    amount = formatRupiah(summary.balance),
                    tint = Color.White
                )
            }
        }

        item(key = "categories-heading") {
            Text(
                text = "Spending by category",
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (categories.isEmpty()) {
            item(key = "no-expenses") {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No expenses in this period.",
                        color = Color.White
                    )
                }
            }
        } else {
            items(
                items = categories,
                key = { it.category },
                contentType = { "category" }
            ) { category ->
                val fraction = (
                        category.amount.toDouble() /
                                summary.expense.toDouble()
                        ).toFloat().coerceIn(0f, 1f)

                val animatedFraction by animateFloatAsState(
                    targetValue = fraction,
                    animationSpec = tween(450),
                    label = "expense share"
                )

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                ) {
                    SummaryRow(
                        label = category.category,
                        amount = formatRupiah(category.amount),
                        tint = Color.White
                    )

                    Spacer(Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = {
                            animatedFraction
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = AethraColors.Mint,
                        trackColor = Color.White.copy(
                            alpha = 0.10f
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = String.format(
                            Locale.ENGLISH,
                            "%.1f%% of expenses",
                            fraction * 100f
                        ),
                        color = AethraColors.MutedText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amount: String,
    tint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            12.dp
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = AethraColors.MutedText,
            fontSize = 14.sp
        )

        Text(
            text = amount,
            color = tint,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}