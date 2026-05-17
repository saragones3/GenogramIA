package dev.saragones3.genogramia.domain.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

class DateFormatter {
    @OptIn(FormatStringsInDatetimeFormats::class)
    fun formatDate(
        millis: Long,
        pattern: String,
    ): String =
        try {
            val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
            val customFormat = LocalDateTime.Format { byUnicodePattern(pattern) }
            dateTime.format(customFormat)
        } catch (_: Exception) {
            ""
        }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun toMillis(
        dateString: String,
        pattern: String,
    ): Long? =
        try {
            val customFormat = LocalDateTime.Format { byUnicodePattern(pattern) }
            LocalDateTime.parse(dateString, customFormat).toInstant(TimeZone.UTC).toEpochMilliseconds()
        } catch (_: Exception) {
            null
        }
}
