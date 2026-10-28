package ru.astrainteractive.aspekt.module.antiswear.di

import org.bukkit.plugin.Plugin
import ru.astrainteractive.aspekt.module.antiswear.api.SwearRepository
import ru.astrainteractive.aspekt.module.antiswear.event.PacketEventSwearListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal class PacketEventsSwearModule(
    swearRepository: SwearRepository,
    plugin: Plugin
) {
    private val packetEventSwearListener = PacketEventSwearListener(swearRepository)

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            packetEventSwearListener.onEnable(plugin)
        },
        onDisable = {
            packetEventSwearListener.onDisable()
        }
    )
}
