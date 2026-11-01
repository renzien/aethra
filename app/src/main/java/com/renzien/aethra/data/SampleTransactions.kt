package com.renzien.aethra.data

import java.time.LocalDateTime

object SampleTransactions {
    fun create(
        now: LocalDateTime = LocalDateTime.now()
    ): List<Transaction> {
        return listOf(
            Transaction(
                id = "sample-groceries",
                title = "Weekly groceries",
                category = "Groceries",
                amount = 425_000L,
                type = TransactionType.EXPENSE,
                date = now.minusMinutes(15)
            ),
            Transaction(
                id = "sample-coffee",
                title = "Coffee & lunch",
                category = "Food & drinks",
                amount = 200_000L,
                type = TransactionType.EXPENSE,
                date = now.minusHours(3)
            ),
            Transaction(
                id = "sample-freelance",
                title = "Freelance project",
                category = "Freelance",
                amount = 250_000L,
                type = TransactionType.INCOME,
                date = now.minusHours(6)
            ),
            Transaction(
                id = "sample-transport",
                title = "Ride & commute",
                category = "Transport",
                amount = 94_580L,
                type = TransactionType.EXPENSE,
                date = now.minusDays(1)
            ),
            Transaction(
                id = "sample-rent",
                title = "Monthly rent",
                category = "Housing",
                amount = 1_850_000L,
                type = TransactionType.EXPENSE,
                date = now.minusDays(2)
            ),
            Transaction(
                id = "sample-salary",
                title = "Monthly salary",
                category = "Salary",
                amount = 15_000_000L,
                type = TransactionType.INCOME,
                date = now.minusDays(3)
            )
        )
    }
}