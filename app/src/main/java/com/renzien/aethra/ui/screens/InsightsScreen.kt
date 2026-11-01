package com.renzien.aethra.ui.screens

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.aethra.data.Transaction
import com.renzien.aethra.data.calculateExpenseCategories
import com.renzien.aethra.data.calculateSummary
import com.renzien.aethra.ui.components.AethraLogo
import com.renzien.aethra.ui.components.GlassCard
import com.renzien.aethra.ui.formatRupiah
import com.renzien.aethra.ui.theme.AethraColors
import java.util.Locale

@Composable
fun InsightsScreen(
    transactions: List<Transaction>,
    bottomContentPadding: Dp = 116.dp
) {
    val summary = remember(transactions) { calculateSummary(transactions) }
    val categories = remember(transactions) { calculateExpenseCategories(transactions) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        contentPadding = PaddingValues(start = 24.dp, top = 32.dp, end = 24.dp, bottom = bottomContentPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "insights-header") {
            Column {
                AethraLogo()
                Spacer(Modifier.height(12.dp))
                Text("Insights", color = Color.White, fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                Text("A clearer picture of your spending.", color = AethraColors.MutedText, fontSize = 14.sp)
            }
        }

        item(key = "cash-flow-summary") {
            GlassCard(Modifier.fillMaxWidth()) {
                Text("Cash flow", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(20.dp))
                SummaryRow("Income", formatRupiah(summary.income), AethraColors.Mint)
                Spacer(Modifier.height(12.dp))
                SummaryRow("Expense", formatRupiah(summary.expense), Color(0xFFFFB9B3))
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.16f))
                Spacer(Modifier.height(16.dp))
                SummaryRow("Balance", formatRupiah(summary.balance), Color.White)
            }
        }

        item(key = "categories-heading") {
            Text("Spending by category", color = Color.White, fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold)
        }

        if (categories.isEmpty()) {
            item(key = "no-expenses") {
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("No expenses yet.", color = Color.White)
                }
            }
        } else {
            items(categories, key = { it.category }, contentType = { "category" }) { category ->
                val fraction = (category.amount.toDouble() / summary.expense.toDouble()).toFloat().coerceIn(0f, 1f)
                GlassCard(Modifier.fillMaxWidth()) {
                    SummaryRow(category.category, formatRupiah(category.amount), Color.White)
                    Spacer(Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth(),
                        color = AethraColors.Mint,
                        trackColor = Color.White.copy(alpha = 0.10f)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = String.format(Locale.ENGLISH, "%.1f%% of expenses", fraction * 100f),
                        color = AethraColors.MutedText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: String, tint: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(label, modifier = Modifier.weight(1f), color = AethraColors.MutedText, fontSize = 14.sp)
        Text(amount, color = tint, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
