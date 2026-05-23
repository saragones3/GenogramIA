package dev.saragones3.genogramia.data.util

import dev.saragones3.genogramia.domain.util.DateProvider
import kotlin.time.Clock
import kotlin.time.Instant

class RealDateProvider(
    private val clock: Clock = Clock.System,
) : DateProvider {
    override fun nowEpochMilliseconds(): Long = clock.now().toEpochMilliseconds()

    override fun now(): Instant = clock.now()
}
