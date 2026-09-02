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
import ru.astrainteractive.aspekt.module.jail.service.UnJailServiceTask
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.service.IntervalService
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import java.io.File
import kotlin.time.Duration.Companion.seconds

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

    private val unJailService = IntervalService(
        interval = UN_JAIL_CHECK_INTERVAL,
        scope = coreModule.ioScope,
        logger = JUtiltLogger("UnJailService"),
        task = UnJailServiceTask(
            cachedJailApi = cachedJailApi,
            jailApi = jailApi,
            jailController = jailController,
            kyoriKrate = coreModule.kyoriKrate,
            translationKrate = coreModule.translationKrate
        )
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            jailEvent.onEnable(bukkitCoreModule.plugin)
            jailCommandModule.lifecycle.onEnable()
            unJailService.onEnable()
        },
        onDisable = {
            unJailService.onDisable()
            jailCommandModule.lifecycle.onDisable()
            jailEvent.onDisable()
            jailController.cancel()
        }
    )

    companion object {
        private val UN_JAIL_CHECK_INTERVAL = 10.seconds

        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("jail.yml")
    }
}
