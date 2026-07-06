package dev.saragones3.genogramia.util

import java.util.Locale

actual fun getAppLanguage(): String = Locale.getDefault().language
