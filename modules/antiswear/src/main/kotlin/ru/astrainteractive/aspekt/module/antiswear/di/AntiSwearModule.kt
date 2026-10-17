package ru.astrainteractive.aspekt.module.antiswear.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.command.di.AntiSwearCommandModule
import ru.astrainteractive.aspekt.module.antiswear.event.AntiSwearEventListener
import ru.astrainteractive.aspekt.module.antiswear.krate.KrateSwearRepository
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class AntiSwearModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val swearRepository = KrateSwearRepository(
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

    private val packetEventsSwearModule: PacketEventsSwearModule? by lazy {
        if (isPacketEventsEnabled) {
            PacketEventsSwearModule(
                swearRepository = swearRepository,
                plugin = bukkitCoreModule.plugin
            )
        } else {
            null
        }
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            antiSwearEventListener.onEnable(bukkitCoreModule.plugin)
            antiSwearCommandModule.lifecycle.onEnable()
            if (isPacketEventsEnabled) packetEventsSwearModule?.lifecycle?.onEnable()
        },
        onDisable = {
            if (isPacketEventsEnabled) packetEventsSwearModule?.lifecycle?.onDisable()
            antiSwearCommandModule.lifecycle.onDisable()
            antiSwearEventListener.onDisable()
            swearRepository.clear()
        }
    )

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("antiswear.yml")
    }
}
