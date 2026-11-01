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
            // ID yang sama tidak boleh menghasilkan transaksi ganda.
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
}