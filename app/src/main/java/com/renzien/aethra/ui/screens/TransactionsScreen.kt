package com.renzien.aethra.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.renzien.aethra.data.TransactionType
import com.renzien.aethra.ui.components.AethraLogo
import com.renzien.aethra.ui.components.GlassCard
import com.renzien.aethra.ui.components.TransactionItem
import com.renzien.aethra.ui.theme.AethraColors

private enum class TransactionFilter(
    val label: String,
    val type: TransactionType?
) {
    All("All", null),
    Income("Income", TransactionType.INCOME),
    Expense("Expense", TransactionType.EXPENSE)
}

@Composable
fun TransactionsScreen(
    transactions: List<Transaction>,
    bottomContentPadding: Dp = 116.dp,
    onTransactionClick: (Transaction) -> Unit = {}
) {
    var filter by rememberSaveable {
        mutableStateOf(TransactionFilter.All)
    }

    val visibleTransactions = remember(transactions, filter) {
        transactions
            .filter {
                filter.type == null || it.type == filter.type
            }
            .sortedByDescending { it.date }
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
        item(key = "transactions-header") {
            Column {
                AethraLogo()

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Transactions",
                    color = Color.White,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Your income and expenses.",
                    color = AethraColors.MutedText,
                    fontSize = 14.sp
                )
            }
        }

        item(key = "transaction-filters") {
            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState()
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp
                )
            ) {
                TransactionFilter.entries.forEach { option ->
                    FilterChip(
                        selected = filter == option,
                        onClick = {
                            filter = option
                        },
                        label = {
                            Text(option.label)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(
                                alpha = 0.06f
                            ),
                            labelColor = AethraColors.MutedText,
                            selectedContainerColor = Color.White.copy(
                                alpha = 0.18f
                            ),
                            selectedLabelColor = Color.White
                        ),
                        border = BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.18f)
                        )
                    )
                }
            }
        }

        if (visibleTransactions.isEmpty()) {
            item(key = "empty-filter") {
                GlassCard(Modifier.fillMaxWidth()) {
                    Text(
                        text = "No transactions here yet.",
                        color = Color.White
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Try another filter.",
                        color = AethraColors.MutedText,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(
                items = visibleTransactions,
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
    }
}