package dev.saragones3.genogramia.presentation.util

import androidx.compose.runtime.Composable
import dev.saragones3.genogramia.domain.util.DateFormatter
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed class UiText {
    data class DynamicString(
        val value: String,
    ) : UiText()

    class Resource(
        val res: StringResource,
        val args: Array<out Any> = emptyArray(),
    ) : UiText() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Resource) return false
            if (res != other.res) return false
            if (!args.contentEquals(other.args)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = res.hashCode()
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    data class DateFormat(
        val millis: Long,
        val patternRes: StringResource,
        val format: (Long, String) -> String,
    ) : UiText()

    @Composable
    fun asString(): String =
        when (this) {
            is DynamicString -> value
            is Resource -> stringResource(res, *args)
            is DateFormat -> format.invoke(millis, stringResource(patternRes))
        }
}
