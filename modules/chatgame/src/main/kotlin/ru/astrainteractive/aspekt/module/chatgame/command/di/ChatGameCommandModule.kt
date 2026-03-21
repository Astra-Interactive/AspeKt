package ru.astrainteractive.aspekt.module.chatgame.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.chatgame.command.quiz.ChatGameLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStore
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for ChatGame module.
 */
internal class ChatGameCommandModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val chatGameStore: ChatGameStore,
    private val chatGameConfig: ChatGameConfig
) {
    private val nodes = buildList {
        ChatGameLiteralArgumentBuilder(
            chatGameStore = chatGameStore,
            translationKrate = coreModule.translation,
            kyoriKrate = coreModule.kyoriKrate,
            chatGameConfig = chatGameConfig,
            currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory,
            ioScope = coreModule.ioScope,
            multiplatformCommand = coreModule.multiplatformCommand
        ).create().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
