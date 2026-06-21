package ru.astrainteractive.aspekt.module.playtimereward.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.klibs.mikro.extensions.serialization.DurationSerializer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Serializable
internal data class PlaytimeRewardConfiguration(
    @SerialName("enabled")
    val enabled: Boolean = true,
    @SerialName("reward_amount")
    val rewardAmount: Double = 100.0,
    @SerialName("required_minutes")
    @Serializable(DurationSerializer::class)
    val requiredDuration: Duration = 60.minutes,
    @SerialName("check_interval_seconds")
    @Serializable(DurationSerializer::class)
    val checkInterval: Duration = 60.seconds,
)
