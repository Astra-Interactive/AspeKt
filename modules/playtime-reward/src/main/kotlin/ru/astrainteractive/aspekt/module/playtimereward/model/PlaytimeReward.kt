package ru.astrainteractive.aspekt.module.playtimereward.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.klibs.mikro.extensions.serialization.JUuidSerializer
import ru.astrainteractive.klibs.mikro.extensions.serialization.KInstantSerializer
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
internal data class PlaytimeReward(
    @SerialName("time_played")
    val timePlayed: Duration,
    @SerialName("uuid")
    @Serializable(JUuidSerializer::class)
    val uuid: UUID,
    @SerialName("latest_username")
    val latestUsername: String,
    @SerialName("latest_join")
    @Serializable(KInstantSerializer::class)
    val latestJoin: Instant
)
