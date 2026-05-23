package dev.saragones3.genogramia.fakes

import dev.saragones3.genogramia.domain.util.DateProvider
import kotlin.time.Instant

class FakeDateProvider : DateProvider {
    var currentTimeMillis: Long = 0L

    override fun nowEpochMilliseconds(): Long = currentTimeMillis

    override fun now(): Instant = Instant.fromEpochMilliseconds(currentTimeMillis)
}
