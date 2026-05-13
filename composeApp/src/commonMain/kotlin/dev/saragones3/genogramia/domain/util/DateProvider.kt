package dev.saragones3.genogramia.domain.util

interface DateProvider {
    fun nowFormatted(): String

    fun nowEpochMilliseconds(): Long
}
