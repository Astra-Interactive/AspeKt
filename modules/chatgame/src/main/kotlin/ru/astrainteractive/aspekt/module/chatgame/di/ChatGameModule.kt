package ru.astrainteractive.aspekt.module.chatgame.di

import kotlinx.coroutines.flow.map
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.chatgame.command.di.ChatGameCommandModule
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.service.ChatGameServiceTask
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStoreImpl
import ru.astrainteractive.aspekt.module.chatgame.store.generator.RiddleGenerator
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.service.IntervalService
import ru.astrainteractive.klibs.kstorage.api.asStateFlowKrate
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import java.io.File

class ChatGameModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val chatGameConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = getConfigurationFile(coreModule.dataFolder),
            factory = ::ChatGameConfig
        )
        .asStateFlowKrate()

    private val chatGameStore = ChatGameStoreImpl(
        chatGameConfigProvider = chatGameConfigKrate,
        riddleGenerator = RiddleGenerator(
            configKrate = chatGameConfigKrate,
            translationKrate = coreModule.translationKrate
        )
    )

    private val chatGameServiceTask = ChatGameServiceTask(
        chatGameStore = chatGameStore,
        chatGameConfigKrate = chatGameConfigKrate,
        kyoriKrate = coreModule.kyoriKrate,
    )

    private val chatGameService = IntervalService(
        interval = chatGameConfigKrate.cachedStateFlow.map { config -> config.timer.delay },
        scope = coreModule.ioScope,
        logger = JUtiltLogger("ChatGameService"),
        task = chatGameServiceTask,
        getInitialDelay = { chatGameConfigKrate.cachedValue.timer.initialDelay }
    )

    private val chatGameCommandModule = ChatGameCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        chatGameStore = chatGameStore,
        chatGameConfigKrate = chatGameConfigKrate
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            chatGameService.onEnable()
            chatGameCommandModule.lifecycle.onEnable()
        },
        onReload = {
            chatGameConfigKrate.getValue()
            chatGameService.onReload()
        },
        onDisable = {
            chatGameCommandModule.lifecycle.onDisable()
            chatGameService.onDisable()
        }
    )

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("chat_game.yml")
    }
}
