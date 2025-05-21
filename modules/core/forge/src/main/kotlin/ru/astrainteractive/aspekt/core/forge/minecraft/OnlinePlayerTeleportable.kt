package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.minecraft.Teleportable
import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

class OnlinePlayerTeleportable(private val instance: OnlineMinecraftPlayer) : Teleportable {
    override fun teleport(location: Location) {
        val player = ForgeUtil.getOnlinePlayer(instance.uuid) ?: return
        val level = ForgeUtil.serverOrNull
            ?.allLevels
            ?.firstOrNull { (it.level.levelData as ServerLevelData).levelName == location.worldName }
            ?: return
        player.teleportTo(
            level,
            location.x,
            location.y,
            location.z,
            0f,
            0f
        )
    }

    @AutoService(Teleportable.Factory::class)
    class Factory : Teleportable.Factory<OnlineMinecraftPlayer> {
        override fun from(instance: OnlineMinecraftPlayer): Teleportable {
            return OnlinePlayerTeleportable(instance)
        }
    }
}
