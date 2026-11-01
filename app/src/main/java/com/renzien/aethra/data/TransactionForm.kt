package com.renzien.aethra.data

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

data class TransactionDraft(
    val title: String,
    val category: String,
    val amountInput: String,
    val type: TransactionType,
    val date: LocalDate
)

data class TransactionFormErrors(
    val title: String? = null,
    val category: String? = null,
    val amount: String? = null,
    val general: String? = null
) {
    val hasErrors: Boolean
        get() = title != null || category != null || amount != null || general != null
}

sealed interface AddTransactionResult {
    data class Added(val transactions: List<Transaction>) : AddTransactionResult
    data class Rejected(val errors: TransactionFormErrors) : AddTransactionResult
}

fun transactionCategories(type: TransactionType): List<String> = when (type) {
    TransactionType.INCOME -> listOf("Salary", "Freelance", "Gift", "Other income")
    TransactionType.EXPENSE -> listOf(
        "Groceries", "Food & drinks", "Transport", "Housing", "Shopping",
        "Health", "Entertainment", "Other expense"
    )
}

fun validateTransactionDraft(draft: TransactionDraft): TransactionFormErrors {
    val input = draft.amountInput.trim()
    val amountError = when {
        input.isEmpty() -> "Enter an amount."
        input.any { it !in '0'..'9' } -> "Use whole rupiah without dots, commas, or minus signs."
        input.toLongOrNull() == null -> "This amount is too large."
        input.toLong() <= 0L -> "Amount must be greater than zero."
        else -> null
    }

    return TransactionFormErrors(
        title = if (draft.title.isBlank()) "Enter a transaction name." else null,
        category = if (draft.category !in transactionCategories(draft.type)) {
            "Choose a category."
        } else null,
        amount = amountError
    )
}

fun addTransaction(
    transactions: List<Transaction>,
    draft: TransactionDraft,
    now: LocalDateTime = LocalDateTime.now(),
    id: String = UUID.randomUUID().toString()
): AddTransactionResult {
    val errors = validateTransactionDraft(draft)
    if (errors.hasErrors) return AddTransactionResult.Rejected(errors)

    val transaction = Transaction(
        id = id,
        title = draft.title.trim(),
        category = draft.category,
        amount = draft.amountInput.trim().toLong(),
        type = draft.type,
        date = draft.date.atTime(now.toLocalTime())
    )
    val updated = transactions + transaction

    // Reject before changing state if an income/expense total would overflow Long.
    try {
        calculateSummary(updated)
    } catch (_: ArithmeticException) {
        return AddTransactionResult.Rejected(
            TransactionFormErrors(amount = "This amount would make your total too large.")
        )
    }

    return AddTransactionResult.Added(updated)
}

// Material DatePicker represents calendar days at midnight UTC.
fun LocalDate.toDatePickerMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun dateFromDatePickerMillis(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
