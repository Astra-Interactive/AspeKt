package ru.astrainteractive.aspekt.module.jail.di

import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApiImpl
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApiImpl
import ru.astrainteractive.aspekt.module.jail.event.JailEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class JailModule(coreModule: CoreModule) {
    private val jailApi: JailApi = JailApiImpl(
        folder = coreModule.plugin.dataFolder.resolve("jail"),
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
        cachedJailApi = cachedJailApi,
        jailController = jailController
    )

    private val jailCommandManager = JailCommandManager(
        scope = coreModule.scope,
        plugin = coreModule.plugin,
        translationKrate = coreModule.translation,
        kyoriKrate = coreModule.kyoriComponentSerializer,
        jailApi = jailApi,
        cachedJailApi = cachedJailApi,
        jailController = jailController
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            jailEvent.onEnable(coreModule.plugin)
            jailCommandManager.register()
        },
        onDisable = {
            jailEvent.onDisable()
            jailController.cancel()
        }
    )
}
