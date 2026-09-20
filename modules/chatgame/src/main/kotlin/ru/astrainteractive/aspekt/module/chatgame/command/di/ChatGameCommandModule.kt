package ru.astrainteractive.aspekt.module.chatgame.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.chatgame.command.quiz.ChatGameLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStore
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

/**
 * Aggregates and registers Brigadier command nodes for ChatGame module.
 */
internal class ChatGameCommandModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val chatGameStore: ChatGameStore,
    private val chatGameConfigKrate: CachedKrate<ChatGameConfig>
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = buildList {
        ChatGameLiteralArgumentBuilder(
            chatGameStore = chatGameStore,
            translationKrate = coreModule.translationKrate,
            kyoriKrate = coreModule.kyoriKrate,
            chatGameConfigKrate = chatGameConfigKrate,
            currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory,
            ioScope = coreModule.ioScope,
            multiplatformCommand = coreModule.multiplatformCommand
        ).create().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
