package ru.astrainteractive.aspekt.module.chatgame.model

import ru.astrainteractive.astralibs.localization.component.LocalizableComponent

internal data class ChatGameData(
    val question: LocalizableComponent,
    val answers: List<String>,
    val reward: Reward
)
