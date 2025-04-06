package ru.astrainteractive.aspekt.module.antiswear.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.command.SwearCommandRegistry
import ru.astrainteractive.aspekt.module.antiswear.command.di.SwearCommandDependencies
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepositoryImpl
import ru.astrainteractive.aspekt.module.antiswear.di.factory.PacketEventSwearListenerFactory
import ru.astrainteractive.aspekt.module.antiswear.event.AntiSwearEventListener
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface AntiSwearModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : AntiSwearModule {
        private val swearRepository = SwearRepositoryImpl(
            dispatchers = coreModule.dispatchers,
            tempFileStringFormat = coreModule.jsonStringFormat
        )
        private val antiSwearEventListener = AntiSwearEventListener(
            swearRepository = swearRepository,
            scope = coreModule.scope
        )
        private val swearCommandRegistry = SwearCommandRegistry(
            dependencies = SwearCommandDependencies.Default(
                coreModule = coreModule,
                swearRepository = swearRepository,
                bukkitCoreModule = bukkitCoreModule
            )
        )

        private val isPacketEventsEnabled: Boolean
            get() = Bukkit.getPluginManager().isPluginEnabled("packetevents")

        private val packetEventSwearListener: EventListener? by lazy {
            if (isPacketEventsEnabled) {
                PacketEventSwearListenerFactory(
                    swearRepository = swearRepository
                ).create()
            } else {
                null
            }
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                antiSwearEventListener.onEnable(bukkitCoreModule.plugin)
                swearCommandRegistry.register()
                if (isPacketEventsEnabled) packetEventSwearListener?.onEnable(bukkitCoreModule.plugin)
            },
            onDisable = {
                antiSwearEventListener.onDisable()
                swearRepository.clear()
                if (isPacketEventsEnabled) packetEventSwearListener?.onDisable()
            }
        )
    }
}
