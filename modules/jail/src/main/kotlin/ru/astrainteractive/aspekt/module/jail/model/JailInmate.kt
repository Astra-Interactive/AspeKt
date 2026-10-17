package ru.astrainteractive.aspekt.module.jail.model

import kotlinx.serialization.Serializable
import java.time.Instant
import kotlin.time.Duration

@Serializable
internal data class JailInmate(
    val uuid: String,
    val lastUsername: String,
    val jailName: String,
    val duration: Duration,
    @Serializable(InstantSerializer::class)
    val start: Instant,
    val lastLocation: JailLocation
) {
    fun isSentenceOver(now: Instant): Boolean = now.epochSecond - start.epochSecond > duration.inWholeSeconds
}
