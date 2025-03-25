package ru.astrainteractive.aspekt.module.jail.command

import ru.astrainteractive.astralibs.command.api.argumenttype.PrimitiveArgumentType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal data object DurationArgumentType : PrimitiveArgumentType<Duration> {
    override val key: String = "DURATION"

    enum class Delimiter(val value: String) {
        W("w"),
        D("d"),
        H("h"),
        M("m"),
        S("s")
    }

    private inline fun <T> Iterable<T>.sumOf(selector: (T) -> Duration): Duration {
        var sum: Duration = 0.seconds
        for (element in this) {
            sum += selector(element)
        }
        return sum
    }

    // 1 year 2 month 3 weeks 4 days 5 hours 10 minutes 30 seconds
    // 3w4d6h10m30s
    override fun transform(value: String): Duration {
        val spl = value
            .replace(Delimiter.W.value, Delimiter.W.value.plus(" "))
            .replace(Delimiter.D.value, Delimiter.D.value.plus(" "))
            .replace(Delimiter.H.value, Delimiter.H.value.plus(" "))
            .replace(Delimiter.M.value, Delimiter.M.value.plus(" "))
            .replace(Delimiter.S.value, Delimiter.S.value.plus(" "))
            .split(" ")
            .filter { it.isNotBlank() }
        val durationList = spl.map { part ->
            val delimiter = Delimiter.entries
                .firstOrNull { delimiter -> part.contains(delimiter.value) }
                ?: error("Wrong usage on argument. Coult not determine delimiter $value. Should be as 1y2mo3w4d6h10m30s")

            val intAmount = part
                .replace(delimiter.value, "")
                .toIntOrNull()
                ?: error("Wrong usage on argument. Could not convert to int $value. Should be as 1y2mo3w4d6h10m30s")

            when (delimiter) {
                Delimiter.W -> (intAmount * 7).days
                Delimiter.D -> intAmount.days
                Delimiter.H -> intAmount.hours
                Delimiter.M -> intAmount.minutes
                Delimiter.S -> intAmount.seconds
            }
        }
        return durationList.sumOf { duration -> duration }
    }
}
