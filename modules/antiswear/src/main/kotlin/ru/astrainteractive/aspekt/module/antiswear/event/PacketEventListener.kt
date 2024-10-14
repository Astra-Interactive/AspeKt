package ru.astrainteractive.aspekt.module.antiswear.event

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketListenerPriority
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.event.EventListener

internal abstract class PacketEventListener(
    priority: PacketListenerPriority = PacketListenerPriority.NORMAL
) : EventListener, PacketListener {

    private val abstract by lazy {
        asAbstract(priority)
    }

    override fun onEnable(plugin: Plugin) {
        super<EventListener>.onEnable(plugin)
        PacketEvents.getAPI().eventManager.registerListener(abstract)
        if (!PacketEvents.getAPI().isInitialized) {
            PacketEvents.getAPI().init()
        }
    }

    override fun onDisable() {
        super<EventListener>.onDisable()
        PacketEvents.getAPI().eventManager.unregisterListeners(abstract)
    }
}
