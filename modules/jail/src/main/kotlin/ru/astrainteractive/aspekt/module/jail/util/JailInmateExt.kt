package ru.astrainteractive.aspekt.module.jail.util

import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import kotlin.time.Instant

internal fun JailInmate.isSentenceOver(now: Instant): Boolean {
    return now.minus(start) > duration
}
