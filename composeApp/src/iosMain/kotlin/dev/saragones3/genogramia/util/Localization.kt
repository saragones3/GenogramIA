package dev.saragones3.genogramia.util

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getAppLanguage(): String = NSLocale.currentLocale.languageCode
