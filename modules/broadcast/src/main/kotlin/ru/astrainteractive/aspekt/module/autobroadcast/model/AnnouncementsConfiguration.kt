package ru.astrainteractive.aspekt.module.autobroadcast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
internal data class AnnouncementsConfiguration(
    @SerialName("interval")
    val interval: Long = 1000L,
    @SerialName("announcements")
    val announcements: Map<String, Announcement> = emptyMap()
) {
    @Serializable
    sealed interface Announcement {
        @Serializable
        @SerialName("TEXT")
        data class Text(
            val text: StringDesc.Raw,
        ) : Announcement

        @Serializable
        @SerialName("ACTION_BAR")
        data class ActionBar(
            val text: StringDesc.Raw,
        ) : Announcement

        @Serializable
        @SerialName("BOSS_BAR")
        data class BossBar(
            val text: StringDesc.Raw,
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
