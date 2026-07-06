package dev.saragones3.genogramia.util

import kotlinx.browser.window

actual fun getAppLanguage(): String = window.navigator.language.split("-")[0]
