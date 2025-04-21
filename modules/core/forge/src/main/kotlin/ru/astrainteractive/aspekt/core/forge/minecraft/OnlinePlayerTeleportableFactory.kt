package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.minecraft.Teleportable
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

@AutoService(Teleportable.Factory::class)
class OnlinePlayerTeleportableFactory : Teleportable.Factory<OnlineMinecraftPlayer> {
    override fun from(instance: OnlineMinecraftPlayer): Teleportable {
        return Teleportable { location ->
            val player = ForgeUtil.getOnlinePlayer(instance.uuid) ?: return@Teleportable
            val level = ForgeUtil.serverOrNull
                ?.allLevels
                ?.firstOrNull { (it.level.levelData as ServerLevelData).levelName == location.worldName }
                ?: return@Teleportable
            player.teleportTo(
                level,
                location.x,
                location.y,
                location.z,
                0f,
                0f
            )
        }
    }
}
