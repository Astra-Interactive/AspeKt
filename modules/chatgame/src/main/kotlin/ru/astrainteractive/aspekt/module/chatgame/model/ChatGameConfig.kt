package ru.astrainteractive.aspekt.module.chatgame.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Serializable
internal data class ChatGameConfig(
    @SerialName("chat_games")
    val chatGames: List<ChatGame> = listOf(
        ChatGame.Riddle(
            question = StringDesc.Raw("Висит груша нельзя скушать"),
            answer = "Лампа",
        ),
        ChatGame.SumOfTwo(),
        ChatGame.TimesOfTwo(),
        ChatGame.EquationEasy(),
    ),
    @SerialName("timer")
    val timer: Timer = Timer(),
    val isEnabled: Boolean = false,
    val defaultReward: Reward = Reward.Money(10.0)
) {
    @Serializable
    data class Timer(
        val initialDelaySeconds: Long = 10.seconds.inWholeSeconds,
        val delaySeconds: Long = 5.minutes.inWholeSeconds
    ) {
        val initialDelay: Duration
            get() = initialDelaySeconds.seconds
        val delay: Duration
            get() = delaySeconds.seconds
    }
}
