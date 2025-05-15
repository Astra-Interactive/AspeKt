package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import org.bukkit.Bukkit
import org.bukkit.Location
import ru.astrainteractive.aspekt.minecraft.Teleportable
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

class OnlinePlayerTeleportable(private val instance: OnlineMinecraftPlayer) : Teleportable {
    override fun teleport(location: ru.astrainteractive.aspekt.minecraft.location.Location) {
        val player = Bukkit.getPlayer(instance.uuid) ?: error("$instance is not online")
        val world = Bukkit.getWorld(location.worldName) ?: error("${location.worldName} world is not found")
        player.teleport(
            Location(
                world,
                location.x,
                location.y,
                location.z,
            )
        )
    }

    @AutoService(Teleportable.Factory::class)
    class Factory : Teleportable.Factory<OnlineMinecraftPlayer> {
        override fun from(instance: OnlineMinecraftPlayer): Teleportable {
            return OnlinePlayerTeleportable(instance)
        }
    }
}
