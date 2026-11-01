package com.renzien.aethra.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.renzien.aethra.data.Transaction
import com.renzien.aethra.data.TransactionType
import com.renzien.aethra.ui.TransactionDeleteState
import com.renzien.aethra.ui.formatRupiah
import com.renzien.aethra.ui.theme.AethraColors
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailSheet(
    transactionId: String,
    transaction: Transaction?,
    deleteState: TransactionDeleteState,
    canDismiss: () -> Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClearDeleteError: () -> Unit
) {
    val isDeleting =
        deleteState == TransactionDeleteState.Deleting(
            transactionId
        )

    val isDeleted =
        deleteState == TransactionDeleteState.Deleted(
            transactionId
        )

    val latestCanDismiss by rememberUpdatedState(canDismiss)

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = {
            it != SheetValue.Hidden || latestCanDismiss()
        }
    )

    val scope = rememberCoroutineScope()

    var confirmDelete by rememberSaveable(transactionId) {
        mutableStateOf(false)
    }

    var isClosing by rememberSaveable(transactionId) {
        mutableStateOf(false)
    }

    var lastKnown by remember(transactionId) {
        mutableStateOf(transaction)
    }

    val shown = transaction ?: lastKnown

    val error = (
            deleteState as? TransactionDeleteState.Failed
            )?.takeIf {
            it.id == transactionId
        }?.message

    LaunchedEffect(transaction) {
        if (transaction != null) {
            lastKnown = transaction
        }
    }

    LaunchedEffect(Unit) {
        if (isClosing) {
            onDismiss()
        }
    }

    fun closeSheet(
        after: () -> Unit = onDismiss
    ) {
        if (isClosing || !latestCanDismiss()) {
            return
        }

        isClosing = true

        scope.launch {
            sheetState.hide()

            if (!sheetState.isVisible) {
                after()
            }
        }
    }

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            confirmDelete = false
            closeSheet()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            if (!isClosing && latestCanDismiss()) {
                onDismiss()
            }
        },
        sheetState = sheetState,
        sheetGesturesEnabled = !isClosing && !isDeleting,
        shape = RoundedCornerShape(
            topStart = 32.dp,
            topEnd = 32.dp
        ),
        containerColor = AethraColors.Navy,
        contentColor = Color.White,
        scrimColor = AethraColors.Navy.copy(alpha = 0.6f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color.White.copy(alpha = 0.28f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(
                20.dp
            )
        ) {
            Text(
                text = "Transaction details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (shown != null) {
                val income =
                    shown.type == TransactionType.INCOME

                val tint = if (income) {
                    AethraColors.Mint
                } else {
                    Color(0xFFFFB9B3)
                }

                GlassCard {
                    Text(
                        text = if (income) {
                            "Income"
                        } else {
                            "Expense"
                        },
                        color = tint
                    )

                    Text(
                        text = if (income) {
                            "+${formatRupiah(shown.amount)}"
                        } else {
                            formatRupiah(shown.signedAmount)
                        },
                        color = tint,
                        fontSize = 30.sp,
                        lineHeight = 38.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = shown.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                DetailRow(
                    label = "Category",
                    value = shown.category
                )

                DetailRow(
                    label = "Date",
                    value = shown.date.format(
                        DateTimeFormatter.ofPattern(
                            "dd MMM yyyy, HH:mm",
                            Locale.ENGLISH
                        )
                    )
                )

                Button(
                    onClick = {
                        closeSheet(onEdit)
                    },
                    enabled = transaction != null &&
                            !isDeleting &&
                            !isDeleted &&
                            !isClosing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = AethraColors.Navy
                    )
                ) {
                    Text(
                        text = "Edit transaction",
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        onClearDeleteError()
                        confirmDelete = true
                    },
                    enabled = transaction != null &&
                            !isDeleting &&
                            !isDeleted &&
                            !isClosing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Delete transaction",
                        color = Color(0xFFFFB9B3)
                    )
                }
            } else {
                Text(
                    text = "This transaction is no longer available.",
                    color = AethraColors.MutedText
                )

                TextButton(
                    onClick = {
                        closeSheet()
                    },
                    enabled = !isDeleting
                ) {
                    Text(
                        text = "Close",
                        color = Color.White
                    )
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = {
                if (latestCanDismiss()) {
                    confirmDelete = false
                }
            },
            title = {
                Text("Delete transaction?")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        12.dp
                    )
                ) {
                    Text(
                        "This removes the transaction and updates your balance."
                    )

                    error?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFFB9B3)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDelete,
                    enabled = !isDeleting
                ) {
                    Text(
                        text = if (isDeleting) {
                            "Deleting..."
                        } else {
                            "Delete"
                        },
                        color = Color(0xFFFFB9B3)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                    },
                    enabled = !isDeleting
                ) {
                    Text(
                        text = "Cancel",
                        color = AethraColors.Mint
                    )
                }
            },
            containerColor = AethraColors.Navy,
            titleContentColor = Color.White,
            textContentColor = AethraColors.MutedText,
            properties = DialogProperties(
                dismissOnBackPress = !isDeleting,
                dismissOnClickOutside = !isDeleting
            )
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            16.dp
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = AethraColors.MutedText
        )

        Text(
            text = value,
            modifier = Modifier.weight(2f),
            color = Color.White
        )
    }
}