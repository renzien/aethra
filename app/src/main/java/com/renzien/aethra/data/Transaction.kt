package com.renzien.aethra.data

import java.time.LocalDateTime

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class Transaction(
    val id: String,
    val title: String,
    val category: String,
    val amount: Long,
    val type: TransactionType,
    val date: LocalDateTime
) {
    init {
        require(id.isNotBlank()) { "Transaction ID must not be blank." }
        require(title.isNotBlank()) { "Transaction title must not be blank." }
        require(category.isNotBlank()) { "Transaction category must not be blank." }
        require(amount > 0L) { "Transaction amount must be positive." }
    }

    val signedAmount: Long
        get() = if (type == TransactionType.INCOME) amount else -amount
}

data class TransactionSummary(
    val income: Long,
    val expense: Long
) {
    val balance: Long
        get() = Math.subtractExact(income, expense)
}

fun calculateSummary(transactions: List<Transaction>): TransactionSummary {
    var income = 0L
    var expense = 0L

    transactions.forEach { transaction ->
        when (transaction.type) {
            TransactionType.INCOME -> {
                income = Math.addExact(income, transaction.amount)
            }
            TransactionType.EXPENSE -> {
                expense = Math.addExact(expense, transaction.amount)
            }
        }
    }

    return TransactionSummary(income = income, expense = expense)
}


data class ExpenseCategory(
    val category: String,
    val amount: Long
)

fun calculateExpenseCategories(transactions: List<Transaction>): List<ExpenseCategory> {
    val totals = linkedMapOf<String, Long>()
    transactions.forEach { transaction ->
        if (transaction.type == TransactionType.EXPENSE) {
            totals[transaction.category] = Math.addExact(
                totals[transaction.category] ?: 0L,
                transaction.amount
            )
        }
    }
    return totals.map { (category, amount) -> ExpenseCategory(category, amount) }
        .sortedByDescending { it.amount }
}
