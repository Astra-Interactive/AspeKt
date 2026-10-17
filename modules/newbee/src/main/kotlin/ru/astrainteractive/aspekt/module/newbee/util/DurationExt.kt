package ru.astrainteractive.aspekt.module.newbee.util

import kotlin.time.Duration

private const val TICKS_IN_SECOND = 20

internal val Duration.ticks: Int
    get() = (inWholeSeconds * TICKS_IN_SECOND).toInt()
