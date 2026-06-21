package ru.astrainteractive.aspekt.module.oregeneration.event

import org.bukkit.Server
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.world.WorldInitEvent
import org.bukkit.plugin.Plugin
import ru.astrainteractive.aspekt.module.oregeneration.populator.OreGenerationBlockPopulator
import ru.astrainteractive.astralibs.event.EventListener

internal class OreGenerationEvent(
    private val server: Server,
    private val populator: OreGenerationBlockPopulator
) : EventListener {

    private fun attachPopulator(world: World) {
        if (world.populators.contains(populator)) return
        world.populators.add(populator)
    }

    @EventHandler
    fun onWorldInit(event: WorldInitEvent) {
        attachPopulator(event.world)
    }

    override fun onEnable(plugin: Plugin) {
        super.onEnable(plugin)
        server.worlds.forEach(::attachPopulator)
    }

    override fun onDisable() {
        super.onDisable()
        server.worlds.forEach { world -> world.populators.remove(populator) }
    }
}
