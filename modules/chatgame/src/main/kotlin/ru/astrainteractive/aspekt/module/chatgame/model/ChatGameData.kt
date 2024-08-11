package ru.astrainteractive.aspekt.module.chatgame.model

import ru.astrainteractive.astralibs.string.StringDesc

internal class ChatGameData(
    val question: StringDesc.Raw,
    val answer: String,
    val reward: Reward
)