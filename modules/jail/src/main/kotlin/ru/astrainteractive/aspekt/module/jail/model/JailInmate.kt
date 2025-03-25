package ru.astrainteractive.aspekt.module.jail.model

import java.time.Instant
import kotlin.time.Duration
import kotlinx.serialization.Serializable
import ru.astrainteractive.aspekt.module.jail.serialization.InstantSerializer

@Serializable
internal data class JailInmate(
    val uuid: String,
    val jailName: String,
    val duration: Duration,
    @Serializable(InstantSerializer::class)
    val start: Instant
)

