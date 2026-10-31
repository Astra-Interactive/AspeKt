package ru.astrainteractive.aspekt.module.jail.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.jail.api.JailCache
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.command.di.JailCommandModule
import ru.astrainteractive.aspekt.module.jail.event.JailEvent
import ru.astrainteractive.aspekt.module.jail.file.FileJailRepository
import ru.astrainteractive.aspekt.module.jail.memory.InMemoryJailCache
import ru.astrainteractive.aspekt.module.jail.service.UnJailServiceTask
import ru.astrainteractive.aspekt.module.jail.usecase.FreeInmateUseCase
import ru.astrainteractive.aspekt.module.jail.usecase.TeleportInmateToJailUseCase
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.service.IntervalService
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import java.io.File
import kotlin.time.Clock
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
    private val freeInmateUseCase = FreeInmateUseCase(
        jailRepository = jailRepository,
        platformServer = coreModule.platformServer,
        dispatchers = coreModule.dispatchers
    )
    private val teleportInmateToJailUseCase = TeleportInmateToJailUseCase(
        jailRepository = jailRepository,
        platformServer = coreModule.platformServer,
        dispatchers = coreModule.dispatchers
    )
    private val jailEvent = JailEvent(
        scope = coreModule.ioScope,
        jailRepository = jailRepository,
        jailCache = jailCache,
        teleportInmateToJailUseCase = teleportInmateToJailUseCase,
        translationKrate = coreModule.translationKrate
    )

    private val jailCommandModule = JailCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        jailRepository = jailRepository,
        jailCache = jailCache,
        freeInmateUseCase = freeInmateUseCase,
        teleportInmateToJailUseCase = teleportInmateToJailUseCase
    )

    private val unJailService = IntervalService(
        interval = UN_JAIL_CHECK_INTERVAL,
        scope = coreModule.ioScope,
        logger = JUtiltLogger("UnJailService"),
        task = UnJailServiceTask(
            jailCache = jailCache,
            jailRepository = jailRepository,
            freeInmateUseCase = freeInmateUseCase,
            clock = Clock.System,
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
        }
    )

    companion object {
        private val UN_JAIL_CHECK_INTERVAL = 10.seconds

        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("jail.yml")
    }
}
