@file:Suppress("EntryPointShape")

package ru.astrainteractive.aspekt.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.astrainteractive.klibs.mikro.core.coroutines.TickFlow
import java.time.LocalTime
import java.util.EnumSet.range
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

internal class OnlineSimulator(private val scope: CoroutineScope) {
    private sealed interface OnlineRange {
        val min: Int
        val max: Int

        data class Default(
            val startHour: Int,
            val endHour: Int,
            override val min: Int,
            override val max: Int
        ) : OnlineRange

        data class Fallback(
            override val min: Int,
            override val max: Int
        ) : OnlineRange
    }

    private val ranges = listOf(
        OnlineRange.Default(
            startHour = 23,
            endHour = 2,
            min = 2,
            max = 4
        ),
        OnlineRange.Default(
            startHour = 3,
            endHour = 7,
            min = 2,
            max = 6
        ),
        OnlineRange.Default(
            startHour = 8,
            endHour = 12,
            min = 4,
            max = 12
        ),
        OnlineRange.Default(
            startHour = 13,
            endHour = 18,
            min = 6,
            max = 15
        ),
        OnlineRange.Default(
            startHour = 19,
            endHour = 22,
            min = 4,
            max = 15
        )
    )

    @Suppress("MagicNumber")
    private val fallbackRange = OnlineRange.Fallback(2, 4)

    val currentOnlineFlow = TickFlow(30.seconds)
        .map { generateOnline() }
        .stateIn(
            scope,
            SharingStarted.Eagerly,
            1
        )

    private fun OnlineRange.computeRandomOnline(): Int {
        if (max == min) return max
        return Random.nextInt(min, max)
    }

    private fun generateOnline(): Int {
        val currentHour = LocalTime.now().hour
        val currentRange = ranges
            .firstOrNull { range -> currentHour in range.startHour..range.endHour }
            ?: fallbackRange
        return currentRange.computeRandomOnline()
    }
}
