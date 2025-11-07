package ru.astrainteractive.aspekt.module.chatgame.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.di.factory.ConfigKrateFactory
import ru.astrainteractive.aspekt.module.chatgame.command.di.ChatGameCommandModule
import ru.astrainteractive.aspekt.module.chatgame.job.ChatGameJob
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStoreImpl
import ru.astrainteractive.aspekt.module.chatgame.store.generator.RiddleGenerator
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.util.asCachedKrate

class ChatGameModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {

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
        kyoriComponentSerializerProvider = coreModule.kyoriKrate,
    )

    private val chatGameCommandModule = ChatGameCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        chatGameStore = chatGameStore,
        chatGameConfig = config.cachedValue
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            chatGameJob.onEnable()
            chatGameCommandModule.lifecycle.onEnable()
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
