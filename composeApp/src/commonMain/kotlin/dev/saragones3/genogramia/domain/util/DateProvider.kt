package dev.saragones3.genogramia.domain.util

import kotlin.time.Instant

interface DateProvider {
    fun nowEpochMilliseconds(): Long

    fun now(): Instant
}
