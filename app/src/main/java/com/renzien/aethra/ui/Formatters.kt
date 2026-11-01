package com.renzien.aethra.ui

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatRupiah(amount: Long): String {
    val symbols = DecimalFormatSymbols(
        Locale.forLanguageTag("id-ID")
    )

    return DecimalFormat(
        "'Rp'#,##0;'-Rp'#,##0",
        symbols
    ).format(amount)
}

fun formatTransactionDate(
    date: LocalDateTime,
    today: LocalDate = LocalDate.now()
): String {
    val day = date.toLocalDate()

    val label = when (day) {
        today -> "Today"

        today.minusDays(1) -> "Yesterday"

        else -> {
            val pattern = if (day.year == today.year) {
                "dd MMM"
            } else {
                "dd MMM yyyy"
            }

            day.format(
                DateTimeFormatter.ofPattern(
                    pattern,
                    Locale.ENGLISH
                )
            )
        }
    }

    val time = date.format(
        DateTimeFormatter.ofPattern(
            "HH:mm",
            Locale.ENGLISH
        )
    )

    return "$label, $time"
}