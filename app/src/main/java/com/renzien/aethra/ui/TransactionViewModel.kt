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

sealed interface TransactionDeleteState {
    data object Idle : TransactionDeleteState

    data class Deleting(
        val id: String
    ) : TransactionDeleteState

    data class Deleted(
        val id: String
    ) : TransactionDeleteState

    class Failed(
        val id: String,
        val message: String
    ) : TransactionDeleteState
}

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val saveState: TransactionSaveState = TransactionSaveState.Idle,
    val deleteState: TransactionDeleteState = TransactionDeleteState.Idle
) {
    val isBusy: Boolean
        get() = saveState is TransactionSaveState.Saving ||
                deleteState is TransactionDeleteState.Deleting
}

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        TransactionsUiState()
    )

    val uiState = mutableUiState.asStateFlow()

    val isBusy: Boolean
        get() = mutableUiState.value.isBusy

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
        requestId: String,
        transactionId: String? = null
    ) {
        val state = mutableUiState.value

        if (
            state.isLoading ||
            state.loadError != null ||
            state.isBusy
        ) {
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

        mutableUiState.update {
            it.copy(
                saveState = TransactionSaveState.Saving(
                    requestId
                )
            )
        }

        viewModelScope.launch {
            val result = try {
                val failure = if (transactionId == null) {
                    repository.save(draft, requestId)
                } else {
                    repository.update(transactionId, draft)
                }

                if (failure == null) {
                    TransactionSaveState.Saved(requestId)
                } else {
                    TransactionSaveState.Failed(
                        requestId,
                        failure
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                TransactionSaveState.Failed(
                    requestId,
                    TransactionFormErrors(
                        general = "We couldn't save this transaction. Please try again."
                    )
                )
            }

            mutableUiState.update {
                it.copy(saveState = result)
            }
        }
    }

    fun delete(id: String) {
        val state = mutableUiState.value

        if (
            state.isLoading ||
            state.loadError != null ||
            state.isBusy
        ) {
            return
        }

        if (
            state.deleteState ==
            TransactionDeleteState.Deleted(id)
        ) {
            return
        }

        mutableUiState.update {
            it.copy(
                deleteState = TransactionDeleteState.Deleting(
                    id
                )
            )
        }

        viewModelScope.launch {
            val result = try {
                repository.delete(id)
                TransactionDeleteState.Deleted(id)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                TransactionDeleteState.Failed(
                    id = id,
                    message = "We couldn't delete this transaction. Please try again."
                )
            }

            mutableUiState.update {
                it.copy(deleteState = result)
            }
        }
    }

    fun clearSaveResult() {
        if (!isBusy) {
            mutableUiState.update {
                it.copy(
                    saveState = TransactionSaveState.Idle
                )
            }
        }
    }

    fun clearDeleteResult() {
        if (!isBusy) {
            mutableUiState.update {
                it.copy(
                    deleteState = TransactionDeleteState.Idle
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
                        TransactionRepository(
                            AethraDatabase.getInstance(appContext)
                        )
                    ) as T
                }
            }
        }
    }
}