package ru.astrainteractive.aspekt.module.chatgame.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.di.factory.ConfigKrateFactory
import ru.astrainteractive.aspekt.module.chatgame.command.ChatGameCommand
import ru.astrainteractive.aspekt.module.chatgame.job.ChatGameJob
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStoreImpl
import ru.astrainteractive.aspekt.module.chatgame.store.RiddleGenerator
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface ChatGameModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : ChatGameModule {

        private val config = ConfigKrateFactory.create(
            fileNameWithoutExtension = "chat_game",
            stringFormat = coreModule.yamlFormat,
            dataFolder = coreModule.plugin.dataFolder,
            factory = ::ChatGameConfig
        )

        private val chatGameStore by lazy {
            ChatGameStoreImpl(
                chatGameConfigProvider = config,
                riddleGenerator = RiddleGenerator(
                    configProvider = config,
                    translationProvider = coreModule.translation
                )
            )
        }

        private val chatGameJob by lazy {
            ChatGameJob(
                chatGameStore = chatGameStore,
                chatGameConfigProvider = config,
                kyoriComponentSerializerProvider = coreModule.kyoriComponentSerializer,
            )
        }

        private val command by lazy {
            ChatGameCommand(
                plugin = coreModule.plugin,
                chatGameStore = chatGameStore,
                kyoriComponentSerializerProvider = coreModule.kyoriComponentSerializer,
                translationProvider = coreModule.translation,
                scope = coreModule.scope,
                chatGameConfigProvider = config,
                currencyEconomyProviderFactory = coreModule.currencyEconomyProviderFactory
            )
        }
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                chatGameJob.onEnable()
                command.register()
            },
            onDisable = {
                chatGameJob.onDisable()
            },
            onReload = {
                config.loadAndGet()
                chatGameJob.onDisable()
                chatGameJob.onEnable()
            }
        )
    }
}
