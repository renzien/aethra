package com.renzien.aethra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.aethra.data.Transaction
import com.renzien.aethra.data.TransactionSummary
import com.renzien.aethra.data.calculateSummary
import com.renzien.aethra.ui.components.AethraLogo
import com.renzien.aethra.ui.components.GlassCard
import com.renzien.aethra.ui.components.TransactionItem
import com.renzien.aethra.ui.formatRupiah
import com.renzien.aethra.ui.theme.AethraColors

@Composable
fun HomeScreen(
    transactions: List<Transaction>,
    bottomContentPadding: Dp = 116.dp,
    cardSurfaceModifier: Modifier = Modifier,
    cardContentModifier: Modifier = Modifier,
    onTransactionClick: (Transaction) -> Unit = {}
) {
    val latestTransactions = remember(transactions) {
        transactions.sortedByDescending { it.date }
    }

    val summary = remember(transactions) {
        calculateSummary(transactions)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentPadding = PaddingValues(
            start = 24.dp,
            top = 32.dp,
            end = 24.dp,
            bottom = bottomContentPadding
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(
            key = "home-header",
            contentType = "header"
        ) {
            Column {
                AethraLogo()

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Your Finance,\nMore stable.",
                    color = Color.White,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(20.dp))
            }
        }

        item(
            key = "balance-card",
            contentType = "balance"
        ) {
            BalanceCard(
                summary = summary,
                transactionCount = transactions.size,
                surfaceModifier = cardSurfaceModifier,
                contentModifier = cardContentModifier
            )
        }

        item(
            key = "transactions-heading",
            contentType = "heading"
        ) {
            Text(
                text = "Recent transactions",
                modifier = Modifier.padding(
                    top = 20.dp,
                    bottom = 4.dp
                ),
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (latestTransactions.isEmpty()) {
            item(
                key = "empty-transactions",
                contentType = "empty"
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No transactions yet.",
                        color = Color.White
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Your income and expenses will appear here.",
                        color = AethraColors.MutedText,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(
                items = latestTransactions,
                key = { it.id },
                contentType = { "transaction" }
            ) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    modifier = Modifier.animateItem(),
                    onClick = {
                        onTransactionClick(transaction)
                    }
                )
            }
        }

        item(
            key = "sample-data-note",
            contentType = "footer"
        ) {
            Text(
                text = "Stored on this device",
                modifier = Modifier.padding(top = 4.dp),
                color = AethraColors.MutedText,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun BalanceCard(
    summary: TransactionSummary,
    transactionCount: Int,
    surfaceModifier: Modifier,
    contentModifier: Modifier
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        surfaceModifier = surfaceModifier,
        contentModifier = contentModifier
    ) {
        Text(
            text = "Balance Amount",
            color = AethraColors.MutedText,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = formatRupiah(summary.balance),
            color = Color.White,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    AethraColors.Mint.copy(alpha = 0.12f)
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                text = if (transactionCount == 1) {
                    "1 transaction"
                } else {
                    "$transactionCount transactions"
                },
                color = AethraColors.Mint,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(28.dp))

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.16f)
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                16.dp
            )
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Income",
                    color = AethraColors.MutedText,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = formatRupiah(summary.income),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Expense",
                    color = AethraColors.MutedText,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = formatRupiah(summary.expense),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}