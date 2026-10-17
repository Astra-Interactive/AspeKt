package ru.astrainteractive.aspekt.module.jail.command

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class DurationArgumentTypeTest {
    @Test
    fun test() {
        assertEquals(1.seconds, DurationArgumentType.transform("1s"))
        assertEquals(1.minutes, DurationArgumentType.transform("1m"))
        assertEquals(1.hours, DurationArgumentType.transform("1h"))
        assertEquals(1.days, DurationArgumentType.transform("1d"))
        assertEquals(7.days, DurationArgumentType.transform("1w"))
        assertEquals(7.days.plus(1.days), DurationArgumentType.transform("1w1d"))
        assertEquals(
            expected = 7.days.plus(1.days).plus(1.hours).plus(1.minutes).plus(1.seconds),
            actual = DurationArgumentType.transform("1w1d1h1m1s")
        )
    }
}
