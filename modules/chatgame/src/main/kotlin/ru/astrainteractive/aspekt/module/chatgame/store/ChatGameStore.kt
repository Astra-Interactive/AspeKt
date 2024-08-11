package ru.astrainteractive.aspekt.module.chatgame.store

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGame

internal interface ChatGameStore {
    val state: StateFlow<State>

    fun endCurrentGame()
    fun startNextGame()
    fun isAnswerCorrect(answer: String): Boolean
    fun isGameStarted(): Boolean

    sealed interface State {
        data object Pending : State
        data class Started(val chatGame: ChatGame.Riddle) : State
    }
}
