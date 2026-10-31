package ru.astrainteractive.aspekt.module.chatgame.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.core.di.BukkitCoreModule
import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.module.chatgame.api.ChatGameStore
import ru.astrainteractive.aspekt.module.chatgame.command.ChatGameLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.usecase.PayMoneyRewardUseCase
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import kotlin.random.Random

internal class ChatGameCommandModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule,
    chatGameStore: ChatGameStore,
    chatGameConfigKrate: CachedKrate<ChatGameConfig>
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = buildList {
        ChatGameLiteralArgumentBuilder(
            chatGameStore = chatGameStore,
            translationKrate = coreModule.translationKrate,
            chatGameConfigKrate = chatGameConfigKrate,
            payMoneyRewardUseCase = PayMoneyRewardUseCase(
                currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory,
                random = Random.Default
            ),
            ioScope = coreModule.ioScope,
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create().run(::add)
    }

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
