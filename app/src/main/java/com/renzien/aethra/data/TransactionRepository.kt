package com.renzien.aethra.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val database: AethraDatabase
) {
    private val dao = database.transactionDao()

    fun observeTransactions(): Flow<List<Transaction>> =
        dao.observeAll().map { rows ->
            rows.map { it.toTransaction() }.also {
                calculateSummary(it)
            }
        }

    suspend fun save(
        draft: TransactionDraft,
        requestId: String
    ): TransactionFormErrors? {
        val errors = validateTransactionDraft(draft)

        if (errors.hasErrors) {
            return errors
        }

        return database.withTransaction {
            if (dao.findById(requestId) != null) {
                return@withTransaction null
            }

            val current = dao.getAll().map {
                it.toTransaction()
            }

            when (
                val result = addTransaction(
                    transactions = current,
                    draft = draft,
                    id = requestId
                )
            ) {
                is AddTransactionResult.Rejected -> {
                    result.errors
                }

                is AddTransactionResult.Added -> {
                    dao.insert(
                        result.transactions.last().toEntity()
                    )
                    null
                }
            }
        }
    }

    suspend fun update(
        id: String,
        draft: TransactionDraft
    ): TransactionFormErrors? {
        val errors = validateTransactionDraft(draft)

        if (errors.hasErrors) {
            return errors
        }

        return database.withTransaction {
            val original = dao.findById(id)?.toTransaction()
                ?: return@withTransaction TransactionFormErrors(
                    general = "This transaction is no longer available."
                )

            val updated = original.copy(
                title = draft.title.trim(),
                category = draft.category,
                amount = draft.amountInput.trim().toLong(),
                type = draft.type,
                date = draft.date.atTime(
                    original.date.toLocalTime()
                )
            )

            val others = dao.getAll()
                .filterNot { it.id == id }
                .map { it.toTransaction() }

            try {
                calculateSummary(others + updated)
            } catch (_: ArithmeticException) {
                return@withTransaction TransactionFormErrors(
                    amount = "This amount would make your total too large."
                )
            }

            check(dao.update(updated.toEntity()) == 1)

            null
        }
    }

    suspend fun delete(id: String) {
        dao.deleteById(id)
    }
}