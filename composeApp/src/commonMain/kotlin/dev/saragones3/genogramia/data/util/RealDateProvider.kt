package dev.saragones3.genogramia.data.util

import dev.saragones3.genogramia.domain.util.DateProvider
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class RealDateProvider(
    private val clock: Clock = Clock.System,
) : DateProvider {
    @OptIn(FormatStringsInDatetimeFormats::class)
    override fun nowFormatted(): String {
        val dateTime = clock.now().toLocalDateTime(TimeZone.UTC)
        val customFormat = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd'T'HH:mm:ss") }
        return try {
            dateTime.format(customFormat)
        } catch (_: Exception) {
            val year = dateTime.year
            val month =
                dateTime.month.number
                    .toString()
                    .padStart(2, '0')
            val day = dateTime.day.toString().padStart(2, '0')
            val hour = dateTime.hour.toString().padStart(2, '0')
            val minute = dateTime.minute.toString().padStart(2, '0')
            val second = dateTime.second.toString().padStart(2, '0')
            return "$year-$month-${day}T$hour:$minute:$second"
        }
    }

    override fun nowEpochMilliseconds(): Long = clock.now().toEpochMilliseconds()
}
