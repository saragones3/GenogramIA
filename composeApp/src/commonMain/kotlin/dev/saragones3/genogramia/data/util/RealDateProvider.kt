package dev.saragones3.genogramia.data.util

import dev.saragones3.genogramia.domain.util.DateProvider
import kotlin.time.Clock

class RealDateProvider(
    private val clock: Clock = Clock.System,
) : DateProvider {
    override fun nowEpochMilliseconds(): Long = clock.now().toEpochMilliseconds()
}
