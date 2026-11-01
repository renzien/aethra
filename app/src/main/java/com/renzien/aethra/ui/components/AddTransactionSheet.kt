package com.renzien.aethra.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.aethra.data.TransactionDraft
import com.renzien.aethra.data.TransactionFormErrors
import com.renzien.aethra.data.TransactionType
import com.renzien.aethra.data.dateFromDatePickerMillis
import com.renzien.aethra.data.toDatePickerMillis
import com.renzien.aethra.data.transactionCategories
import com.renzien.aethra.ui.formatRupiah
import com.renzien.aethra.ui.theme.AethraColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    onDismiss: () -> Unit,
    onSubmit: (TransactionDraft) -> TransactionFormErrors?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var title by rememberSaveable { mutableStateOf("") }
    var amountInput by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDay by rememberSaveable { mutableStateOf(LocalDate.now().toEpochDay()) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var errors by remember { mutableStateOf(TransactionFormErrors()) }
    var isClosing by rememberSaveable { mutableStateOf(false) }

    // A rotation during the closing animation must not allow a second Save.
    LaunchedEffect(Unit) {
        if (isClosing) onDismiss()
    }

    val selectedDate = LocalDate.ofEpochDay(selectedDay)
    val categories = remember(type) { transactionCategories(type) }
    val colorScheme = remember {
        darkColorScheme(
            primary = AethraColors.Mint,
            onPrimary = AethraColors.Navy,
            primaryContainer = AethraColors.Ocean,
            onPrimaryContainer = Color.White,
            secondary = AethraColors.Mint,
            secondaryContainer = Color(0xFF285676),
            onSecondaryContainer = Color.White,
            surface = AethraColors.Navy,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF183D60),
            onSurfaceVariant = AethraColors.MutedText,
            surfaceContainer = Color(0xFF0B2948),
            surfaceContainerHigh = Color(0xFF163957),
            surfaceContainerHighest = Color(0xFF1D4262),
            outline = Color.White.copy(alpha = 0.32f),
            error = Color(0xFFFFB4AB)
        )
    }

    fun closeSheet() {
        if (isClosing) return
        isClosing = true
        focusManager.clearFocus()
        scope.launch {
            sheetState.hide()
            if (!sheetState.isVisible) onDismiss()
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = MaterialTheme.typography) {
        ModalBottomSheet(
            onDismissRequest = { if (!isClosing) onDismiss() },
            sheetState = sheetState,
            sheetGesturesEnabled = !isClosing,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = AethraColors.Navy,
            contentColor = Color.White,
            scrimColor = AethraColors.Navy.copy(alpha = 0.6f),
            dragHandle = {
                BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.28f))
            }
        ) {
            // The modal handles IME/system insets; scrolling keeps Save reachable.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .background(
                        Brush.verticalGradient(
                            listOf(AethraColors.Navy, AethraColors.Ocean.copy(alpha = 0.5f))
                        )
                    )
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column {
                    Text(
                        "New transaction",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("A small step for a clearer balance.", color = AethraColors.MutedText)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TransactionType.entries.forEach { option ->
                        FilterChip(
                            selected = type == option,
                            enabled = !isClosing,
                            onClick = {
                                if (type != option) {
                                    type = option
                                    category = ""
                                    errors = errors.copy(category = null, general = null)
                                }
                            },
                            label = {
                                Text(if (option == TransactionType.EXPENSE) "Expense" else "Income")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.04f),
                                selectedContainerColor = AethraColors.Mint.copy(alpha = 0.16f),
                                selectedLabelColor = AethraColors.Mint
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                        )
                    }
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = {
                        amountInput = it
                        errors = errors.copy(amount = null, general = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isClosing,
                    singleLine = true,
                    label = { Text("Amount") },
                    prefix = { Text("Rp ") },
                    placeholder = { Text("100000") },
                    textStyle = MaterialTheme.typography.headlineSmall,
                    isError = errors.amount != null,
                    supportingText = {
                        val preview = amountInput.trim().takeIf { input ->
                            input.isNotEmpty() && input.all { it in '0'..'9' }
                        }?.toLongOrNull()?.takeIf { it > 0L }
                        Text(errors.amount ?: preview?.let(::formatRupiah)
                        ?: "Whole rupiah, without dots or commas.")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    shape = RoundedCornerShape(20.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errors = errors.copy(title = null, general = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isClosing,
                    singleLine = true,
                    label = { Text("Transaction name") },
                    placeholder = { Text("For example, lunch with friends") },
                    isError = errors.title != null,
                    supportingText = errors.title?.let { message -> { Text(message) } },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    shape = RoundedCornerShape(20.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { expanded ->
                        if (!isClosing) {
                            focusManager.clearFocus()
                            categoryExpanded = expanded
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = !isClosing
                            ),
                        enabled = !isClosing,
                        readOnly = true,
                        singleLine = true,
                        label = { Text("Category") },
                        placeholder = { Text("Choose a category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        isError = errors.category != null,
                        supportingText = errors.category?.let { message -> { Text(message) } },
                        shape = RoundedCornerShape(20.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                    errors = errors.copy(category = null, general = null)
                                }
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        showDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    enabled = !isClosing,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.32f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date", color = AethraColors.MutedText)
                        Text(
                            selectedDate.format(
                                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
                            ),
                            color = Color.White
                        )
                    }
                }

                errors.general?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        if (!isClosing) {
                            val failure = onSubmit(
                                TransactionDraft(title, category, amountInput, type, selectedDate)
                            )
                            if (failure == null) closeSheet() else errors = failure
                        }
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    enabled = !isClosing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = AethraColors.Navy
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Save transaction", fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = { closeSheet() },
                    enabled = !isClosing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = AethraColors.MutedText)
                }
            }
        }

        if (showDatePicker && !isClosing) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.toDatePickerMillis()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        enabled = datePickerState.selectedDateMillis != null,
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedDay = dateFromDatePickerMillis(it).toEpochDay()
                            }
                            showDatePicker = false
                        }
                    ) { Text("Use date") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
