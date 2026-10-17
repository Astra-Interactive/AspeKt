package ru.astrainteractive.aspekt.module.jail.di

import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.jail.api.JailCache
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.command.di.JailCommandModule
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.event.JailEvent
import ru.astrainteractive.aspekt.module.jail.file.FileJailRepository
import ru.astrainteractive.aspekt.module.jail.memory.InMemoryJailCache
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
    private val jailRepository: JailRepository = FileJailRepository(
        folder = coreModule.dataFolder.resolve("jail"),
        stringFormat = coreModule.yamlFormat
    )
    private val jailCache: JailCache = InMemoryJailCache(
        jailRepository = jailRepository,
        scope = coreModule.ioScope
    )
    private val jailController = JailController(
        dispatchers = coreModule.dispatchers,
        jailRepository = jailRepository
    )
    private val jailEvent = JailEvent(
        scope = coreModule.ioScope,
        jailRepository = jailRepository,
        jailCache = jailCache,
        jailController = jailController,
        translationKrate = coreModule.translationKrate
    )

    private val jailCommandModule = JailCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        jailRepository = jailRepository,
        jailCache = jailCache,
        jailController = jailController
    )

    private val unJailService = IntervalService(
        interval = UN_JAIL_CHECK_INTERVAL,
        scope = coreModule.ioScope,
        logger = JUtiltLogger("UnJailService"),
        task = UnJailServiceTask(
            jailCache = jailCache,
            jailRepository = jailRepository,
            jailController = jailController,
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
