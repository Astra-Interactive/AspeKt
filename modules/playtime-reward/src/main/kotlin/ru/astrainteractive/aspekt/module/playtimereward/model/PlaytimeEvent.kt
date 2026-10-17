package ru.astrainteractive.aspekt.module.playtimereward.model

internal sealed interface PlaytimeEvent {
    data object Joined : PlaytimeEvent

    data object Left : PlaytimeEvent

    data object Checked : PlaytimeEvent
}
