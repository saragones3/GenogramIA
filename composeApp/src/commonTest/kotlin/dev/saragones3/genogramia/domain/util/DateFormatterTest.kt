package dev.saragones3.genogramia.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals

class DateFormatterTest {
    private val dateFormatter = DateFormatter()

    @Test
    fun `GIVEN millis WHEN formatting date THEN returns correct string`() {
        val millis = 1778716800000L // 14-may-2026

        assertEquals("14/05/2026", dateFormatter.formatDate(millis, "dd/MM/yyyy"))
        assertEquals("05/14/2026", dateFormatter.formatDate(millis, "MM/dd/yyyy"))
    }

    @Test
    fun `GIVEN old date millis WHEN formatting date THEN returns correct string`() {
        val millis = -3144182400000 // 14-may-1870

        assertEquals("14/05/1870", dateFormatter.formatDate(millis, "dd/MM/yyyy"))
        assertEquals("05/14/1870", dateFormatter.formatDate(millis, "MM/dd/yyyy"))
    }
}
