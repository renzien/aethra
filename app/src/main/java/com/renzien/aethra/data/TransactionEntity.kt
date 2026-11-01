package com.renzien.aethra.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val amount: Long,
    val type: String,
    val date: String
)

fun TransactionEntity.toTransaction(): Transaction = Transaction(
    id = id,
    title = title,
    category = category,
    amount = amount,
    type = TransactionType.valueOf(type),
    date = LocalDateTime.parse(date)
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    title = title,
    category = category,
    amount = amount,
    type = type.name,
    date = date.toString()
)