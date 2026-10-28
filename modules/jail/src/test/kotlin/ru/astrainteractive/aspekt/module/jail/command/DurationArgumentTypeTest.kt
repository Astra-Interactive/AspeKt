@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.jail.command

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class DurationArgumentTypeTest {
    @Test
    fun GIVEN_single_unit_WHEN_transformed_THEN_returns_that_duration() {
        assertEquals(1.seconds, DurationArgumentType.transform("1s"))
        assertEquals(1.minutes, DurationArgumentType.transform("1m"))
        assertEquals(1.hours, DurationArgumentType.transform("1h"))
        assertEquals(1.days, DurationArgumentType.transform("1d"))
        assertEquals(7.days, DurationArgumentType.transform("1w"))
    }

    @Test
    fun GIVEN_several_units_WHEN_transformed_THEN_returns_their_sum() {
        assertEquals(7.days.plus(1.days), DurationArgumentType.transform("1w1d"))
        val weekAndDay = 7.days.plus(1.days)
        val withHours = weekAndDay.plus(1.hours)
        val withMinutes = withHours.plus(1.minutes)
        val expected = withMinutes.plus(1.seconds)
        assertEquals(
            expected = expected,
            actual = DurationArgumentType.transform("1w1d1h1m1s")
        )
    }
}
