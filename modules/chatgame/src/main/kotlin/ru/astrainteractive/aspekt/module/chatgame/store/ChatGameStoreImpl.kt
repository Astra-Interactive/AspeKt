package ru.astrainteractive.aspekt.module.chatgame.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal class ChatGameStoreImpl(
    chatGameConfigProvider: Provider<ChatGameConfig>,
    private val riddleGenerator: RiddleGenerator
) : ChatGameStore, Logger by JUtiltLogger("ChatGameStore") {
    private val chatGameConfig by chatGameConfigProvider

    private val _state = MutableStateFlow<ChatGameStore.State>(ChatGameStore.State.Pending)
    override val state: StateFlow<ChatGameStore.State> = _state.asStateFlow()

    override fun endCurrentGame() {
        _state.value = ChatGameStore.State.Pending
    }

    override fun startNextGame() {
        val nextGame = chatGameConfig.chatGames.randomOrNull() ?: run {
            error { "#startNextGame could not start chat game" }
            return
        }
        val game = riddleGenerator.generate(nextGame)
        _state.value = ChatGameStore.State.Started(game)
    }

    override fun isAnswerCorrect(answer: String): Boolean {
        val currentGame = state.value as? ChatGameStore.State.Started ?: return false
        return answer.equals(currentGame.chatGame.answer, true)
    }

    override fun isGameStarted(): Boolean {
        return state.value is ChatGameStore.State.Started
    }
}
