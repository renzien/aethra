package com.renzien.aethra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.aethra.data.Transaction
import com.renzien.aethra.ui.formatTransactionPeriod
import com.renzien.aethra.ui.transactionMonths
import com.renzien.aethra.ui.theme.AethraColors
import java.time.YearMonth

@Composable
fun PeriodSelector(
    transactions: List<Transaction>,
    selectedMonth: YearMonth?,
    onMonthSelected: (YearMonth?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val currentMonth = YearMonth.now()

    val months = remember(
        transactions,
        selectedMonth,
        currentMonth
    ) {
        transactionMonths(
            transactions = transactions,
            selectedMonth = selectedMonth,
            currentMonth = currentMonth
        )
    }

    val previous = remember(selectedMonth) {
        selectedMonth?.let {
            runCatching {
                it.minusMonths(1)
            }.getOrNull()
        }
    }

    val next = remember(selectedMonth) {
        selectedMonth?.let {
            runCatching {
                it.plusMonths(1)
            }.getOrNull()
        }
    }

    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Color.White.copy(alpha = 0.08f)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.16f),
                shape = shape
            )
            .padding(10.dp)
    ) {
        Text(
            text = "Period",
            modifier = Modifier.padding(
                start = 12.dp,
                top = 4.dp
            ),
            color = AethraColors.MutedText,
            fontSize = 12.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    previous?.let(onMonthSelected)
                },
                enabled = previous != null
            ) {
                MonthArrow(
                    forward = false,
                    enabled = previous != null
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                TextButton(
                    onClick = {
                        expanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = formatTransactionPeriod(
                            selectedMonth
                        ),
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.width(6.dp))

                    Canvas(
                        modifier = Modifier.size(10.dp)
                    ) {
                        val stroke = 1.6.dp.toPx()

                        drawLine(
                            color = Color.White,
                            start = Offset(
                                0f,
                                size.height * 0.3f
                            ),
                            end = Offset(
                                size.width / 2,
                                size.height * 0.8f
                            ),
                            strokeWidth = stroke,
                            cap = StrokeCap.Round
                        )

                        drawLine(
                            color = Color.White,
                            start = Offset(
                                size.width / 2,
                                size.height * 0.8f
                            ),
                            end = Offset(
                                size.width,
                                size.height * 0.3f
                            ),
                            strokeWidth = stroke,
                            cap = StrokeCap.Round
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                    modifier = Modifier.heightIn(
                        max = 320.dp
                    ),
                    containerColor = AethraColors.Navy
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "All time",
                                color = if (selectedMonth == null) {
                                    AethraColors.Mint
                                } else {
                                    Color.White
                                }
                            )
                        },
                        modifier = Modifier.semantics {
                            selected = selectedMonth == null
                        },
                        onClick = {
                            expanded = false
                            onMonthSelected(null)
                        }
                    )

                    months.forEach { month ->
                        DropdownMenuItem(
                            text = {
                                val label =
                                    formatTransactionPeriod(month) +
                                            if (month == currentMonth) {
                                                " (This month)"
                                            } else {
                                                ""
                                            }

                                Text(
                                    text = label,
                                    color = if (month == selectedMonth) {
                                        AethraColors.Mint
                                    } else {
                                        Color.White
                                    }
                                )
                            },
                            modifier = Modifier.semantics {
                                selected = month == selectedMonth
                            },
                            onClick = {
                                expanded = false
                                onMonthSelected(month)
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    next?.let(onMonthSelected)
                },
                enabled = next != null
            ) {
                MonthArrow(
                    forward = true,
                    enabled = next != null
                )
            }
        }
    }
}

@Composable
private fun MonthArrow(
    forward: Boolean,
    enabled: Boolean
) {
    Canvas(
        modifier = Modifier
            .size(18.dp)
            .semantics {
                contentDescription = if (forward) {
                    "Next month"
                } else {
                    "Previous month"
                }
            }
    ) {
        val color = Color.White.copy(
            alpha = if (enabled) 1f else 0.3f
        )

        val outerX = size.width * if (forward) {
            0.35f
        } else {
            0.65f
        }

        val pointX = size.width * if (forward) {
            0.7f
        } else {
            0.3f
        }

        drawLine(
            color = color,
            start = Offset(
                outerX,
                size.height * 0.2f
            ),
            end = Offset(
                pointX,
                size.height * 0.5f
            ),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                pointX,
                size.height * 0.5f
            ),
            end = Offset(
                outerX,
                size.height * 0.8f
            ),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}