package dev.saragones3.genogramia.presentation.util

import dev.saragones3.genogramia.domain.model.GenogramTree
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.date_days_ago
import genogramia.composeapp.generated.resources.date_format
import genogramia.composeapp.generated.resources.date_today
import genogramia.composeapp.generated.resources.date_yesterday
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun GenogramTree.formatLastUpdated(
    now: Instant,
    format: (Long, String) -> String,
): UiText =
    try {
        // Assume lastUpdated is in ISO format and UTC
        val lastUpdatedDateTime = LocalDateTime.parse(lastUpdated)
        val lastUpdatedInstant = lastUpdatedDateTime.toInstant(TimeZone.UTC)

        val userTimeZone = TimeZone.currentSystemDefault()
        val nowKmp = Instant.fromEpochMilliseconds(now.toEpochMilliseconds())
        val today = nowKmp.toLocalDateTime(userTimeZone).date
        val lastUpdatedDate = lastUpdatedInstant.toLocalDateTime(userTimeZone).date

        when (val daysBetween = lastUpdatedDate.daysUntil(today)) {
            0 -> {
                UiText.Resource(Res.string.date_today)
            }

            1 -> {
                UiText.Resource(Res.string.date_yesterday)
            }

            in 2..6 -> {
                UiText.Resource(Res.string.date_days_ago, arrayOf(daysBetween))
            }

            else -> {
                UiText.DateFormat(
                    millis = lastUpdatedInstant.toEpochMilliseconds(),
                    patternRes = Res.string.date_format,
                    format = format,
                )
            }
        }
    } catch (_: Exception) {
        UiText.DynamicString(lastUpdated)
    }
