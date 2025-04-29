package ru.astrainteractive.aspekt.module.chatgame.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.di.factory.ConfigKrateFactory
import ru.astrainteractive.aspekt.module.chatgame.command.ChatGameCommand
import ru.astrainteractive.aspekt.module.chatgame.job.ChatGameJob
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStoreImpl
import ru.astrainteractive.aspekt.module.chatgame.store.generator.RiddleGenerator
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.util.asCachedKrate

interface ChatGameModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : ChatGameModule {

        private val config = ConfigKrateFactory.fileConfigKrate(
            file = coreModule.dataFolder.resolve("chat_game.yml"),
            stringFormat = coreModule.yamlFormat,
            factory = ::ChatGameConfig
        ).asCachedKrate()

        private val chatGameStore = ChatGameStoreImpl(
            chatGameConfigProvider = config,
            riddleGenerator = RiddleGenerator(
                configKrate = config,
                translationKrate = coreModule.translation
            )
        )

        private val chatGameJob = ChatGameJob(
            chatGameStore = chatGameStore,
            chatGameConfigProvider = config,
            kyoriComponentSerializerProvider = coreModule.kyoriComponentSerializer,
        )

        private val command = ChatGameCommand(
            plugin = bukkitCoreModule.plugin,
            chatGameStore = chatGameStore,
            kyoriComponentSerializerProvider = coreModule.kyoriComponentSerializer,
            translationProvider = coreModule.translation,
            scope = coreModule.scope,
            chatGameConfigProvider = config,
            currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                chatGameJob.onEnable()
                command.register()
            },
            onDisable = {
                chatGameJob.onDisable()
            },
            onReload = {
                config.getValue()
                chatGameJob.onDisable()
                chatGameJob.onEnable()
            }
        )
    }
}
