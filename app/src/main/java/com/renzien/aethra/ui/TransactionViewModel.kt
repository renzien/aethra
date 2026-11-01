package com.renzien.aethra.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.renzien.aethra.data.AethraDatabase
import com.renzien.aethra.data.Transaction
import com.renzien.aethra.data.TransactionDraft
import com.renzien.aethra.data.TransactionFormErrors
import com.renzien.aethra.data.TransactionRepository
import com.renzien.aethra.data.validateTransactionDraft
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface TransactionSaveState {
    data object Idle : TransactionSaveState

    data class Saving(
        val requestId: String
    ) : TransactionSaveState

    data class Saved(
        val requestId: String
    ) : TransactionSaveState

    class Failed(
        val requestId: String,
        val errors: TransactionFormErrors
    ) : TransactionSaveState
}

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val saveState: TransactionSaveState = TransactionSaveState.Idle
)

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        TransactionsUiState()
    )

    val uiState = mutableUiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        retryLoading()
    }

    fun retryLoading() {
        loadJob?.cancel()

        mutableUiState.update {
            it.copy(
                isLoading = true,
                loadError = null
            )
        }

        loadJob = viewModelScope.launch {
            repository.observeTransactions()
                .catch { error ->
                    if (error is CancellationException) {
                        throw error
                    }

                    mutableUiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = "We couldn't load your transactions. Please try again."
                        )
                    }
                }
                .collect { transactions ->
                    mutableUiState.update {
                        it.copy(
                            transactions = transactions,
                            isLoading = false,
                            loadError = null
                        )
                    }
                }
        }
    }

    fun submit(
        draft: TransactionDraft,
        requestId: String
    ) {
        val state = mutableUiState.value

        if (state.isLoading || state.loadError != null) {
            return
        }

        if (state.saveState is TransactionSaveState.Saving) {
            return
        }

        if (
            state.saveState ==
            TransactionSaveState.Saved(requestId)
        ) {
            return
        }

        val errors = validateTransactionDraft(draft)

        if (errors.hasErrors) {
            mutableUiState.update {
                it.copy(
                    saveState = TransactionSaveState.Failed(
                        requestId = requestId,
                        errors = errors
                    )
                )
            }
            return
        }

        // Langsung ubah status agar dua tap cepat tidak memulai dua penyimpanan.
        mutableUiState.update {
            it.copy(
                saveState = TransactionSaveState.Saving(
                    requestId
                )
            )
        }

        viewModelScope.launch {
            val result = try {
                val failure = repository.save(
                    draft = draft,
                    requestId = requestId
                )

                if (failure == null) {
                    TransactionSaveState.Saved(requestId)
                } else {
                    TransactionSaveState.Failed(
                        requestId = requestId,
                        errors = failure
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                TransactionSaveState.Failed(
                    requestId = requestId,
                    errors = TransactionFormErrors(
                        general = "We couldn't save this transaction. Please try again."
                    )
                )
            }

            mutableUiState.update {
                it.copy(saveState = result)
            }
        }
    }

    fun clearSaveResult() {
        if (
            mutableUiState.value.saveState !is
                    TransactionSaveState.Saving
        ) {
            mutableUiState.update {
                it.copy(
                    saveState = TransactionSaveState.Idle
                )
            }
        }
    }

    companion object {
        fun factory(
            context: Context
        ): ViewModelProvider.Factory {
            val appContext = context.applicationContext

            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    require(
                        modelClass.isAssignableFrom(
                            TransactionViewModel::class.java
                        )
                    )

                    @Suppress("UNCHECKED_CAST")
                    return TransactionViewModel(
                        repository = TransactionRepository(
                            AethraDatabase.getInstance(appContext)
                        )
                    ) as T
                }
            }
        }
    }
}