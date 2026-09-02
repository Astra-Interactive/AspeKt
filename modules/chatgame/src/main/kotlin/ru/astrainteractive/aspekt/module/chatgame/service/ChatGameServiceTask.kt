package ru.astrainteractive.aspekt.module.chatgame.service

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStore
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.service.ServiceTask
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class ChatGameServiceTask(
    private val chatGameStore: ChatGameStore,
    chatGameConfigKrate: CachedKrate<ChatGameConfig>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
) : ServiceTask {
    private val kyoriComponentSerializer by kyoriKrate
    private val chatGameConfig by chatGameConfigKrate

    override suspend fun execute() {
        if (!chatGameConfig.isEnabled) return
        chatGameStore.startNextGame()
        val state = chatGameStore.state.value as? ChatGameStore.State.Started ?: return
        with(kyoriComponentSerializer) {
            state.chatGame.question
                .component
                .run(Bukkit::broadcast)
        }
    }
}
