package ru.astrainteractive.aspekt.module.chatgame.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.chatgame.command.ChatGameCommand
import ru.astrainteractive.aspekt.module.chatgame.job.ChatGameJob
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStoreImpl
import ru.astrainteractive.aspekt.module.chatgame.store.RiddleGenerator
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrDefault
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.klibs.kdi.Reloadable

interface ChatGameModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : ChatGameModule {
        private val config = Reloadable {
            val file = coreModule.plugin.value.dataFolder.resolve("chat_game.yml")
            val config = coreModule.yamlFormat.parseOrDefault(file, ::ChatGameConfig)
            coreModule.yamlFormat.writeIntoFile(config, file)
            config
        }
        private val chatGameStore by lazy {
            ChatGameStoreImpl(
                chatGameConfigProvider = { config.value },
                riddleGenerator = RiddleGenerator(
                    configProvider = { config.value },
                    translationProvider = { coreModule.translation.value }
                )
            )
        }
        private val chatGameJob by lazy {
            ChatGameJob(
                chatGameStore = chatGameStore,
                chatGameConfigProvider = { config.value },
                kyoriComponentSerializerProvider = { coreModule.kyoriComponentSerializer.value },
            )
        }
        private val command by lazy {
            ChatGameCommand(
                plugin = coreModule.plugin.value,
                chatGameStore = chatGameStore,
                kyoriComponentSerializerProvider = { coreModule.kyoriComponentSerializer.value },
                translationProvider = { coreModule.translation.value },
                economyProvider = { coreModule.economyProvider.value },
                scope = coreModule.scope,
                chatGameConfigProvider = { config.value }
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
                config.reload()
                chatGameJob.onDisable()
                chatGameJob.onEnable()
            }
        )
    }
}
