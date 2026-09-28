package ru.astrainteractive.aspekt.module.autobroadcast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.localization.text.LocalizedText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
internal data class AnnouncementsConfiguration(
    @SerialName("is_enabled")
    val isEnabled: Boolean = false,
    @SerialName("interval")
    val intervalSeconds: Long = 1000L,
    @SerialName("announcements")
    val announcements: Map<String, Announcement> = emptyMap()
) {
    val interval: Duration
        get() = intervalSeconds.seconds

    @Serializable
    sealed interface Announcement {
        val text: LocalizedText

        @Serializable
        @SerialName("TEXT")
        data class Text(
            override val text: LocalizedText,
        ) : Announcement

        @Serializable
        @SerialName("ACTION_BAR")
        data class ActionBar(
            override val text: LocalizedText,
        ) : Announcement

        @Serializable
        @SerialName("BOSS_BAR")
        data class BossBar(
            override val text: LocalizedText,
            val barColor: BarColor = BarColor.BLUE,
            @SerialName("duration_seconds")
            val durationSeconds: Long = 5,
        ) : Announcement {
            val duration: Duration
                get() = durationSeconds.seconds

            enum class BarColor {
                PINK,
                BLUE,
                RED,
                GREEN,
                YELLOW,
                PURPLE,
                WHITE
            }
        }
    }
}
