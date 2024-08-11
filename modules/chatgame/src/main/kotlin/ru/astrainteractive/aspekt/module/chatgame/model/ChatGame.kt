package ru.astrainteractive.aspekt.module.chatgame.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc

@Serializable
@SerialName("CHAT_GAME")
internal sealed interface ChatGame {
    val reward: Reward?

    @SerialName("RIDDLE")
    @Serializable
    class Riddle(
        val question: StringDesc.Raw,
        val answer: String,
        override val reward: Reward? = null
    ) : ChatGame

    @SerialName("SUM_OF_TWO")
    @Serializable
    class SumOfTwo(override val reward: Reward? = null) : ChatGame

    @SerialName("TIMES_OF_TWO")
    @Serializable
    class TimesOfTwo(override val reward: Reward? = null) : ChatGame

    @SerialName("EQUATION_EASY")
    @Serializable
    class EquationEasy(override val reward: Reward? = null) : ChatGame

    @SerialName("EQUATION_QUADRATIC")
    @Serializable
    class QuadraticEquation(override val reward: Reward? = null) : ChatGame

    @SerialName("ANAGRAM")
    @Serializable
    class Anagram(
        val words: List<String>,
        override val reward: Reward? = null
    ) : ChatGame
}
