package ru.astrainteractive.aspekt.core.forge.minecraft.teleport

import com.google.auto.service.AutoService
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.teleport.Teleportable

@AutoService(Teleportable.Factory::class)
class OnlinePlayerTeleportableFactory : Teleportable.Factory<OnlineMinecraftPlayer> {
    override fun from(instance: OnlineMinecraftPlayer): Teleportable {
        return Teleportable { location ->
            // todo impl
        }
    }
}