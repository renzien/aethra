package com.renzien.aethra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.aethra.data.Transaction
import com.renzien.aethra.data.TransactionType
import com.renzien.aethra.ui.formatRupiah
import com.renzien.aethra.ui.formatTransactionDate
import com.renzien.aethra.ui.theme.AethraColors

@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val isIncome = transaction.type == TransactionType.INCOME

    val tint = if (isIncome) {
        AethraColors.Mint
    } else {
        Color(0xFFFFB9B3)
    }

    val amountLabel = if (isIncome) {
        "+${formatRupiah(transaction.amount)}"
    } else {
        formatRupiah(transaction.signedAmount)
    }

    val fontScale = LocalDensity.current.fontScale
    val shape = RoundedCornerShape(20.dp)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
            .clip(shape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f),
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        role = Role.Button,
                        onClickLabel = "View transaction details",
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(16.dp)
    ) {
        val compact = maxWidth < 268.dp || fontScale > 1.2f

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = tint.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                DirectionIcon(
                    isIncome = isIncome,
                    tint = tint
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    4.dp
                )
            ) {
                Text(
                    text = transaction.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = transaction.category,
                    color = AethraColors.MutedText,
                    fontSize = 12.sp
                )

                if (compact) {
                    TransactionAmountAndDate(
                        transaction = transaction,
                        amountLabel = amountLabel,
                        tint = tint
                    )
                }
            }

            if (!compact) {
                Spacer(Modifier.width(12.dp))

                TransactionAmountAndDate(
                    transaction = transaction,
                    amountLabel = amountLabel,
                    tint = tint,
                    alignment = Alignment.End
                )
            }
        }
    }
}

@Composable
private fun TransactionAmountAndDate(
    transaction: Transaction,
    amountLabel: String,
    tint: Color,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = amountLabel,
            color = tint,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = formatTransactionDate(transaction.date),
            color = AethraColors.MutedText,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun DirectionIcon(
    isIncome: Boolean,
    tint: Color
) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val start = if (isIncome) {
            Offset(
                size.width * 0.75f,
                size.height * 0.25f
            )
        } else {
            Offset(
                size.width * 0.25f,
                size.height * 0.75f
            )
        }

        val end = if (isIncome) {
            Offset(
                size.width * 0.25f,
                size.height * 0.75f
            )
        } else {
            Offset(
                size.width * 0.75f,
                size.height * 0.25f
            )
        }

        val direction = if (isIncome) 1f else -1f
        val stroke = 2.dp.toPx()

        drawLine(
            color = tint,
            start = start,
            end = end,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = tint,
            start = end,
            end = Offset(
                end.x + size.width * 0.4f * direction,
                end.y
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = tint,
            start = end,
            end = Offset(
                end.x,
                end.y - size.height * 0.4f * direction
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}