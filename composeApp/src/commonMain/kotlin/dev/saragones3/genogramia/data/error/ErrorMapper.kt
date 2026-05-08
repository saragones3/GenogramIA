package dev.saragones3.genogramia.data.error

import dev.saragones3.genogramia.domain.model.AuthError

expect fun Throwable.toAuthError(): AuthError
