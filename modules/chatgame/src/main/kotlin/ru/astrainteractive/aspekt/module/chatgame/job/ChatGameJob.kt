package ru.astrainteractive.aspekt.module.chatgame.job

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStore
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate

internal class ChatGameJob(
    private val chatGameStore: ChatGameStore,
    chatGameConfigProvider: Krate<ChatGameConfig>,
    kyoriComponentSerializerProvider: Krate<KyoriComponentSerializer>,
) : ScheduledJob("ChatGameJob") {
    private val kyoriComponentSerializer by kyoriComponentSerializerProvider
    private val chatGameConfig by chatGameConfigProvider
    override val delayMillis: Long
        get() = chatGameConfig.timer.delay.inWholeMilliseconds
    override val initialDelayMillis: Long
        get() = chatGameConfig.timer.initialDelay.inWholeMilliseconds
    override val isEnabled: Boolean
        get() = chatGameConfig.isEnabled

    override fun execute() {
        chatGameStore.startNextGame()
        val state = chatGameStore.state.value as? ChatGameStore.State.Started ?: return
        with(kyoriComponentSerializer) {
            state.chatGame.question
                .component
                .run(Bukkit::broadcast)
        }
    }
}
