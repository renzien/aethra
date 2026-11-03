package com.renzien.aethra.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
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

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = 240,
            easing = FastOutSlowInEasing
        ),
        label = "period arrow"
    )

    val shape = RoundedCornerShape(24.dp)

    val surface = remember {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.16f),
                Color.White.copy(alpha = 0.075f),
                Color.White.copy(alpha = 0.045f)
            )
        )
    }

    val border = remember {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.32f),
                Color.White.copy(alpha = 0.10f)
            )
        )
    }

    fun chooseMonth(month: YearMonth?) {
        expanded = false
        onMonthSelected(month)
    }

    BackHandler(enabled = expanded) {
        expanded = false
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(surface)
            .border(
                width = 1.dp,
                brush = border,
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
                    previous?.let(::chooseMonth)
                },
                enabled = previous != null
            ) {
                MonthArrow(
                    forward = false,
                    enabled = previous != null
                )
            }

            TextButton(
                onClick = {
                    expanded = !expanded
                },
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        stateDescription = if (expanded) {
                            "Expanded"
                        } else {
                            "Collapsed"
                        }
                    }
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
                    modifier = Modifier
                        .size(10.dp)
                        .rotate(arrowRotation)
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

            IconButton(
                onClick = {
                    next?.let(::chooseMonth)
                },
                enabled = next != null
            ) {
                MonthArrow(
                    forward = true,
                    enabled = next != null
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(
                    durationMillis = 280,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(180)
            ),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(
                    durationMillis = 220,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(120)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    color = Color.White.copy(
                        alpha = 0.12f
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 268.dp)
                        .verticalScroll(rememberScrollState())
                        .selectableGroup()
                        .padding(bottom = 4.dp)
                ) {
                    PeriodOption(
                        label = "All time",
                        selected = selectedMonth == null,
                        onClick = {
                            chooseMonth(null)
                        }
                    )

                    months.forEach { month ->
                        PeriodOption(
                            label = formatTransactionPeriod(month) +
                                    if (month == currentMonth) {
                                        " (This month)"
                                    } else {
                                        ""
                                    },
                            selected = month == selectedMonth,
                            onClick = {
                                chooseMonth(month)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (selected) {
            Color.White.copy(alpha = 0.12f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(160),
        label = "period selection"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 2.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .heightIn(min = 48.dp)
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = if (selected) {
                AethraColors.Mint
            } else {
                Color.White
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            }
        )

        if (selected) {
            Spacer(Modifier.width(12.dp))

            Canvas(
                modifier = Modifier.size(18.dp)
            ) {
                val stroke = 2.dp.toPx()

                drawLine(
                    color = AethraColors.Mint,
                    start = Offset(
                        size.width * 0.15f,
                        size.height * 0.5f
                    ),
                    end = Offset(
                        size.width * 0.4f,
                        size.height * 0.75f
                    ),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = AethraColors.Mint,
                    start = Offset(
                        size.width * 0.4f,
                        size.height * 0.75f
                    ),
                    end = Offset(
                        size.width * 0.85f,
                        size.height * 0.25f
                    ),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
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