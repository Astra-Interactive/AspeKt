package ru.astrainteractive.aspekt.module.chatgame.model

import ru.astrainteractive.astralibs.string.StringDesc

internal data class ChatGameData(
    val question: StringDesc,
    val answers: List<String>,
    val reward: Reward
)
