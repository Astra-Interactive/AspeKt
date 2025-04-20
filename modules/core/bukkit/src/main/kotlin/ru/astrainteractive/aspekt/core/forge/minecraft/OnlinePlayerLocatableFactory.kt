package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.minecraft.Locatable
import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

@AutoService(Locatable.Factory::class)
class OnlinePlayerLocatableFactory : Locatable.Factory<OnlineMinecraftPlayer> {
    override fun from(instance: OnlineMinecraftPlayer): Locatable {
        return Locatable {
            val player = Bukkit.getPlayer(instance.uuid) ?: error("$instance is not online")
            Location(
                x = player.location.x,
                y = player.location.y,
                z = player.location.z,
                worldName = player.location.world.name
            )
        }
    }
}
