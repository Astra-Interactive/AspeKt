package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import org.bukkit.Bukkit
import org.bukkit.Location
import ru.astrainteractive.aspekt.minecraft.Teleportable
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

@AutoService(Teleportable.Factory::class)
class OnlinePlayerTeleportableFactory : Teleportable.Factory<OnlineMinecraftPlayer> {
    override fun from(instance: OnlineMinecraftPlayer): Teleportable {
        return Teleportable { location ->
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
    }
}
