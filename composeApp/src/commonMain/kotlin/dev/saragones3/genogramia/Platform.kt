package dev.saragones3.genogramia

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
