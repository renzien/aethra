package com.renzien.aethra.ui

import androidx.compose.runtime.saveable.Saver
import com.renzien.aethra.data.Transaction
import com.renzien.aethra.data.TransactionType
import java.time.LocalDateTime

// Saved UI state survives Activity recreation. Permanent storage comes next.
val TransactionListSaver = Saver<List<Transaction>, ArrayList<Any>>(
    save = { transactions ->
        ArrayList(transactions.flatMap { transaction ->
            listOf(
                transaction.id,
                transaction.title,
                transaction.category,
                transaction.amount,
                transaction.type.name,
                transaction.date.toString()
            )
        })
    },
    restore = { values ->
        values.chunked(6).map { row ->
            Transaction(
                id = row[0] as String,
                title = row[1] as String,
                category = row[2] as String,
                amount = row[3] as Long,
                type = TransactionType.valueOf(row[4] as String),
                date = LocalDateTime.parse(row[5] as String)
            )
        }
    }
)
