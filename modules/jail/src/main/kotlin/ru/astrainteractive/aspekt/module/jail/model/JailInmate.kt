package ru.astrainteractive.aspekt.module.jail.model

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Instant

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
