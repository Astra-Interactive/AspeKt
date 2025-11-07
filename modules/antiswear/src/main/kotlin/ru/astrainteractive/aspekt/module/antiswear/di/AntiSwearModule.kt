package ru.astrainteractive.aspekt.module.antiswear.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.command.di.AntiSwearCommandModule
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepositoryImpl
import ru.astrainteractive.aspekt.module.antiswear.di.factory.PacketEventSwearListenerFactory
import ru.astrainteractive.aspekt.module.antiswear.event.AntiSwearEventListener
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class AntiSwearModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val swearRepository = SwearRepositoryImpl(
        dispatchers = coreModule.dispatchers,
        tempFileStringFormat = coreModule.jsonStringFormat
    )
    private val antiSwearEventListener = AntiSwearEventListener(
        swearRepository = swearRepository,
        scope = coreModule.ioScope
    )
    private val antiSwearCommandModule = AntiSwearCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        swearRepository = swearRepository
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

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            antiSwearEventListener.onEnable(bukkitCoreModule.plugin)
            antiSwearCommandModule.lifecycle.onEnable()
            if (isPacketEventsEnabled) packetEventSwearListener?.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            antiSwearEventListener.onDisable()
            swearRepository.clear()
            if (isPacketEventsEnabled) packetEventSwearListener?.onDisable()
        }
    )
}
