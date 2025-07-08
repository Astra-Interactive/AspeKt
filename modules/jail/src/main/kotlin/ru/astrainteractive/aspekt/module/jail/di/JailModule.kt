package ru.astrainteractive.aspekt.module.jail.di

import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.module.jail.data.internal.CachedJailApiImpl
import ru.astrainteractive.aspekt.module.jail.data.internal.JailApiImpl
import ru.astrainteractive.aspekt.module.jail.event.JailEvent
import ru.astrainteractive.aspekt.module.jail.job.UnJailJob
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class JailModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val jailApi: JailApi = JailApiImpl(
        folder = coreModule.dataFolder.resolve("jail"),
        stringFormat = coreModule.yamlFormat
    )
    private val cachedJailApi: CachedJailApi = CachedJailApiImpl(
        jailApi = jailApi,
        scope = coreModule.scope
    )
    private val jailController = JailController(
        dispatchers = coreModule.dispatchers,
        jailApi = jailApi
    )
    private val jailEvent = JailEvent(
        scope = coreModule.scope,
        jailApi = jailApi,
        cachedJailApi = cachedJailApi,
        jailController = jailController,
        kyoriKrate = coreModule.kyoriComponentSerializer,
        translationKrate = coreModule.translation
    )

    private val jailCommandManager = JailCommandManager(
        scope = coreModule.scope,
        plugin = bukkitCoreModule.plugin,
        translationKrate = coreModule.translation,
        kyoriKrate = coreModule.kyoriComponentSerializer,
        jailApi = jailApi,
        cachedJailApi = cachedJailApi,
        jailController = jailController,
        commandsRegistrarFlow = bukkitCoreModule.commandsRegistrarFlow,
        mainScope = coreModule.mainScope
    )

    private val unJailJob = UnJailJob(
        scope = coreModule.scope,
        cachedJailApi = cachedJailApi,
        jailApi = jailApi,
        jailController = jailController,
        kyoriKrate = coreModule.kyoriComponentSerializer,
        translationKrate = coreModule.translation
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            jailEvent.onEnable(bukkitCoreModule.plugin)
            jailCommandManager.register()
            unJailJob.onEnable()
        },
        onDisable = {
            jailEvent.onDisable()
            unJailJob.onDisable()
            jailController.cancel()
        }
    )
}
