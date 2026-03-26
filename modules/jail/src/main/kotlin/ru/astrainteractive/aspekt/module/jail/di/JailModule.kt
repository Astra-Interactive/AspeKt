package ru.astrainteractive.aspekt.module.jail.di

import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.jail.command.di.JailCommandModule
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
        scope = coreModule.ioScope
    )
    private val jailController = JailController(
        dispatchers = coreModule.dispatchers,
        jailApi = jailApi
    )
    private val jailEvent = JailEvent(
        scope = coreModule.ioScope,
        jailApi = jailApi,
        cachedJailApi = cachedJailApi,
        jailController = jailController,
        kyoriKrate = coreModule.kyoriKrate,
        translationKrate = coreModule.translationKrate
    )

    private val jailCommandModule = JailCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        jailApi = jailApi,
        cachedJailApi = cachedJailApi,
        jailController = jailController
    )

    private val unJailJob = UnJailJob(
        scope = coreModule.ioScope,
        cachedJailApi = cachedJailApi,
        jailApi = jailApi,
        jailController = jailController,
        kyoriKrate = coreModule.kyoriKrate,
        translationKrate = coreModule.translationKrate
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            jailEvent.onEnable(bukkitCoreModule.plugin)
            jailCommandModule.lifecycle.onEnable()
            unJailJob.onEnable()
        },
        onDisable = {
            jailEvent.onDisable()
            unJailJob.onDisable()
            jailController.cancel()
        }
    )
}
