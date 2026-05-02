package ru.astrainteractive.aspekt.module.chatgame.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.chatgame.command.di.ChatGameCommandModule
import ru.astrainteractive.aspekt.module.chatgame.job.ChatGameJob
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStoreImpl
import ru.astrainteractive.aspekt.module.chatgame.store.generator.RiddleGenerator
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import ru.astrainteractive.klibs.kstorage.api.withDefault

class ChatGameModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val chatGameConfigKrate = coreModule.yamlFormat
        .krateOf<ChatGameConfig>(coreModule.dataFolder.resolve("chat_game.yml"))
        .withDefault(::ChatGameConfig)
        .asCachedKrate()

    private val chatGameStore = ChatGameStoreImpl(
        chatGameConfigProvider = chatGameConfigKrate,
        riddleGenerator = RiddleGenerator(
            configKrate = chatGameConfigKrate,
            translationKrate = coreModule.translationKrate
        )
    )

    private val chatGameJob = ChatGameJob(
        chatGameStore = chatGameStore,
        chatGameConfigProvider = chatGameConfigKrate,
        kyoriComponentSerializerProvider = coreModule.kyoriKrate,
    )

    private val chatGameCommandModule = ChatGameCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        chatGameStore = chatGameStore,
        chatGameConfig = chatGameConfigKrate.cachedValue
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
            chatGameConfigKrate.getValue()
            chatGameJob.onDisable()
            chatGameJob.onEnable()
        }
    )
}
