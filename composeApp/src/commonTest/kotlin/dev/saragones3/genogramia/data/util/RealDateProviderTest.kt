package dev.saragones3.genogramia.data.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

class RealDateProviderTest {
    private val fixedInstant = Instant.fromEpochMilliseconds(1625097600000L) // 2021-07-01T00:00:00
    private val fakeClock =
        object : Clock {
            override fun now(): Instant = fixedInstant
        }
    private val dateProvider = RealDateProvider(fakeClock)

    @Test
    fun `nowFormatted returns correct UTC format`() {
        // When
        val result = dateProvider.nowFormatted()

        // Then
        assertEquals("2021-07-01T00:00:00", result)
    }

    @Test
    fun `nowEpochMilliseconds returns correct timestamp`() {
        // When
        val result = dateProvider.nowEpochMilliseconds()

        // Then
        assertEquals(1625097600000L, result)
    }
}
