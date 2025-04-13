package ru.astrainteractive.aspekt.module.jail.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.aspekt.module.jail.serialization.InstantSerializer
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
)
