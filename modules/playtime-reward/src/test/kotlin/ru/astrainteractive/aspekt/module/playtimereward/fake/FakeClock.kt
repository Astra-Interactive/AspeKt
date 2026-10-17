package ru.astrainteractive.aspekt.module.playtimereward.fake

import kotlin.time.Clock
import kotlin.time.Instant

internal class FakeClock(var current: Instant) : Clock {
    override fun now(): Instant = current
}
