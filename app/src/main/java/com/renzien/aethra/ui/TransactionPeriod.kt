package com.renzien.aethra.ui

import com.renzien.aethra.data.Transaction
import com.renzien.aethra.data.TransactionType
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

fun filterTransactionsByPeriod(
    transactions: List<Transaction>,
    month: YearMonth?,
    type: TransactionType? = null
): List<Transaction> = transactions.filter { transaction ->
    (month == null || YearMonth.from(transaction.date) == month) &&
            (type == null || transaction.type == type)
}

fun transactionMonths(
    transactions: List<Transaction>,
    selectedMonth: YearMonth?,
    currentMonth: YearMonth = YearMonth.now()
): List<YearMonth> = (
        transactions.map { YearMonth.from(it.date) } +
                listOfNotNull(currentMonth, selectedMonth)
        ).distinct().sortedDescending()

fun formatTransactionPeriod(
    month: YearMonth?
): String = month?.format(
    DateTimeFormatter.ofPattern(
        "MMMM uuuu",
        Locale.ENGLISH
    )
) ?: "All time"